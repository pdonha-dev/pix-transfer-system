package com.pdonha.pix.application.service;

import com.pdonha.pix.application.dto.command.CreatePixTransferCommand;
import com.pdonha.pix.application.dto.result.TransferResult;
import com.pdonha.pix.domain.exception.IdempotencyKeyFailedException;
import com.pdonha.pix.domain.exception.IdempotencyKeyInvalidException;
import com.pdonha.pix.domain.exception.IdempotencyKeyStillProcessingException;
import com.pdonha.pix.domain.exception.PixKeyNotFoundException;
import com.pdonha.pix.domain.model.IdempotencyKey;
import com.pdonha.pix.domain.model.IdempotencyStatus;
import com.pdonha.pix.domain.model.Money;
import com.pdonha.pix.domain.model.TransferStatus;
import com.pdonha.pix.domain.port.IdempotencyKeyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdempotencyServiceTest {

    @Mock
    private IdempotencyKeyRepository idempotencyKeyRepository;

    @Mock
    private CreatePixTransferService createPixTransferService;

    @InjectMocks
    private IdempotencyService idempotencyService;

    private String idempotencyKey;
    private CreatePixTransferCommand command;
    private TransferResult transferResult;

    @BeforeEach
    void setUp() {
        idempotencyKey = UUID.randomUUID().toString();
        command = new CreatePixTransferCommand("12345678900", "user@example.com", new BigDecimal("100.00"));
        UUID transferId = UUID.randomUUID();
        transferResult = new TransferResult(
                transferId,
                TransferStatus.PENDING,
                new Money(new BigDecimal("100.00")),
                LocalDateTime.now()
        );
    }

    @Test
    void shouldExecuteTransferWhenKeyDoesNotExist() {
        when(idempotencyKeyRepository.findByKey(idempotencyKey)).thenReturn(Optional.empty());
        when(createPixTransferService.execute(any())).thenReturn(transferResult);

        TransferResult result = idempotencyService.executeWithIdempotency(idempotencyKey, command);

        assertNotNull(result);
        assertEquals(transferResult.getTransferId(), result.getTransferId());
        verify(idempotencyKeyRepository, times(2)).save(any());
        verify(createPixTransferService, times(1)).execute(command);
    }

    @Test
    void shouldReturnCachedResultWhenKeyAlreadyProcessedSuccessfully() {
        IdempotencyKey successKey = new IdempotencyKey(idempotencyKey, transferResult.getTransferId(), IdempotencyStatus.SUCCESS);
        when(idempotencyKeyRepository.findByKey(idempotencyKey)).thenReturn(Optional.of(successKey));
        when(createPixTransferService.getTransferResult(transferResult.getTransferId())).thenReturn(transferResult);

        TransferResult result = idempotencyService.executeWithIdempotency(idempotencyKey, command);

        assertNotNull(result);
        assertEquals(transferResult.getTransferId(), result.getTransferId());
        verify(createPixTransferService, never()).execute(any());
        verify(createPixTransferService, times(1)).getTransferResult(transferResult.getTransferId());
    }

    @Test
    void shouldThrowExceptionWhenKeyStillProcessing() {
        IdempotencyKey pendingKey = new IdempotencyKey(idempotencyKey, UUID.randomUUID(), IdempotencyStatus.PENDING);
        when(idempotencyKeyRepository.findByKey(idempotencyKey)).thenReturn(Optional.of(pendingKey));

        assertThrows(IdempotencyKeyStillProcessingException.class, () ->
                idempotencyService.executeWithIdempotency(idempotencyKey, command)
        );

        verify(createPixTransferService, never()).execute(any());
    }

    @Test
    void shouldThrowExceptionWhenKeyPreviouslyFailed() {
        IdempotencyKey failedKey = new IdempotencyKey(idempotencyKey, UUID.randomUUID(), IdempotencyStatus.FAILED);
        when(idempotencyKeyRepository.findByKey(idempotencyKey)).thenReturn(Optional.of(failedKey));

        assertThrows(IdempotencyKeyFailedException.class, () ->
                idempotencyService.executeWithIdempotency(idempotencyKey, command)
        );

        verify(createPixTransferService, never()).execute(any());
    }

    @Test
    void shouldThrowExceptionWhenIdempotencyKeyIsNull() {
        assertThrows(IdempotencyKeyInvalidException.class, () ->
                idempotencyService.executeWithIdempotency(null, command)
        );

        verify(idempotencyKeyRepository, never()).findByKey(any());
        verify(createPixTransferService, never()).execute(any());
    }

    @Test
    void shouldThrowExceptionWhenIdempotencyKeyIsBlank() {
        assertThrows(IdempotencyKeyInvalidException.class, () ->
                idempotencyService.executeWithIdempotency("   ", command)
        );

        verify(idempotencyKeyRepository, never()).findByKey(any());
        verify(createPixTransferService, never()).execute(any());
    }

    @Test
    void shouldMarkKeyAsFailedWhenExecutionThrowsException() {
        when(idempotencyKeyRepository.findByKey(idempotencyKey)).thenReturn(Optional.empty());
        when(createPixTransferService.execute(any())).thenThrow(new PixKeyNotFoundException("PIX key not found"));

        assertThrows(PixKeyNotFoundException.class, () ->
                idempotencyService.executeWithIdempotency(idempotencyKey, command)
        );

        verify(idempotencyKeyRepository, times(2)).save(any());
    }

    @Test
    void shouldMarkKeyAsSuccessAfterSuccessfulExecution() {
        when(idempotencyKeyRepository.findByKey(idempotencyKey)).thenReturn(Optional.empty());
        when(createPixTransferService.execute(any())).thenReturn(transferResult);

        idempotencyService.executeWithIdempotency(idempotencyKey, command);

        verify(idempotencyKeyRepository, times(2)).save(any());
    }
}
