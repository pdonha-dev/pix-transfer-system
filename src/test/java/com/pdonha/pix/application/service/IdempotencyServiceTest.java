package com.pdonha.pix.application.service;

import com.pdonha.pix.application.dto.command.CreatePixTransferCommand;
import com.pdonha.pix.application.dto.result.TransferResult;
import com.pdonha.pix.domain.exception.IdempotencyKeyConflictException;
import com.pdonha.pix.domain.exception.IdempotencyKeyFailedException;
import com.pdonha.pix.domain.exception.IdempotencyKeyInvalidException;
import com.pdonha.pix.domain.exception.IdempotencyKeyStillProcessingException;
import com.pdonha.pix.domain.exception.PixKeyNotFoundException;
import com.pdonha.pix.domain.model.IdempotencyKey;
import com.pdonha.pix.domain.model.IdempotencyStatus;
import com.pdonha.pix.domain.model.Money;
import com.pdonha.pix.domain.model.TransferStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdempotencyServiceTest {

    @Mock
    private IdempotencyRecordService recordService;
    @Mock
    private IdempotentTransferAttemptService attemptService;
    @Mock
    private CreatePixTransferService transferService;

    private IdempotencyService service;
    private CreatePixTransferCommand command;
    private TransferResult result;

    @BeforeEach
    void setUp() {
        service = new IdempotencyService(recordService, attemptService, transferService);
        command = new CreatePixTransferCommand(
                "12345678900", "user@example.com", new BigDecimal("100.00"));
        result = new TransferResult(
                UUID.randomUUID(), TransferStatus.PENDING,
                new Money(new BigDecimal("100.00")), LocalDateTime.now());
    }

    @Test
    void shouldReserveAndExecuteNewRequest() {
        String key = "new-key";
        when(recordService.findByKey(key)).thenReturn(Optional.empty());
        when(attemptService.execute(eq(key), any(UUID.class), eq(command))).thenReturn(result);

        TransferResult actual = service.executeWithIdempotency(key, command);

        assertEquals(result.getTransferId(), actual.getTransferId());
        verify(recordService).reserve(any(IdempotencyKey.class));
        verify(attemptService).execute(eq(key), any(UUID.class), eq(command));
    }

    @Test
    void shouldReturnPriorResultForSameRequest() {
        String key = "success-key";
        IdempotencyKey record = record(key, IdempotencyStatus.SUCCESS,
                IdempotencyService.requestHash(command), result.getTransferId());
        when(recordService.findByKey(key)).thenReturn(Optional.of(record));
        when(transferService.getTransferResult(result.getTransferId())).thenReturn(result);

        TransferResult actual = service.executeWithIdempotency(key, command);

        assertEquals(result.getTransferId(), actual.getTransferId());
        verify(attemptService, never()).execute(any(), any(), any());
    }

    @Test
    void shouldReplayLegacySuccessfulRecordWithoutPayloadFingerprint() {
        String key = "legacy-key";
        IdempotencyKey record = record(key, IdempotencyStatus.SUCCESS,
                "legacy:" + UUID.randomUUID(), result.getTransferId());
        when(recordService.findByKey(key)).thenReturn(Optional.of(record));
        when(transferService.getTransferResult(result.getTransferId())).thenReturn(result);

        TransferResult actual = service.executeWithIdempotency(key, command);

        assertEquals(result.getTransferId(), actual.getTransferId());
    }

    @Test
    void shouldRejectSameKeyForDifferentRequest() {
        String key = "reused-key";
        when(recordService.findByKey(key)).thenReturn(Optional.of(
                record(key, IdempotencyStatus.SUCCESS, "different-hash", result.getTransferId())));

        assertThrows(IdempotencyKeyConflictException.class,
                () -> service.executeWithIdempotency(key, command));
    }

    @Test
    void shouldRejectPendingAndFailedRequests() {
        String hash = IdempotencyService.requestHash(command);
        when(recordService.findByKey("pending")).thenReturn(Optional.of(
                record("pending", IdempotencyStatus.PENDING, hash, UUID.randomUUID())));
        when(recordService.findByKey("failed")).thenReturn(Optional.of(
                record("failed", IdempotencyStatus.FAILED, hash, UUID.randomUUID())));

        assertThrows(IdempotencyKeyStillProcessingException.class,
                () -> service.executeWithIdempotency("pending", command));
        assertThrows(IdempotencyKeyFailedException.class,
                () -> service.executeWithIdempotency("failed", command));
    }

    @Test
    void shouldRejectBlankKey() {
        assertThrows(IdempotencyKeyInvalidException.class,
                () -> service.executeWithIdempotency(" ", command));
        verify(recordService, never()).findByKey(any());
    }

    @Test
    void shouldMarkReservationFailedWhenTransferFails() {
        String key = "failed-transfer";
        when(recordService.findByKey(key)).thenReturn(Optional.empty());
        when(attemptService.execute(eq(key), any(UUID.class), eq(command)))
                .thenThrow(new PixKeyNotFoundException("PIX key not found"));

        assertThrows(PixKeyNotFoundException.class,
                () -> service.executeWithIdempotency(key, command));

        verify(recordService).markFailed(key);
    }

    private IdempotencyKey record(String key, IdempotencyStatus status,
                                  String hash, UUID transferId) {
        return IdempotencyKey.rehydrate(
                UUID.randomUUID(), key, transferId, hash, status,
                LocalDateTime.now(), LocalDateTime.now());
    }
}
