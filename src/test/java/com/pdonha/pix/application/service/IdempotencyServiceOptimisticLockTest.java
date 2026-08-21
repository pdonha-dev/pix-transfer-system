package com.pdonha.pix.application.service;

import com.pdonha.pix.application.dto.command.CreatePixTransferCommand;
import com.pdonha.pix.application.dto.result.TransferResult;
import com.pdonha.pix.domain.exception.IdempotencyKeyInvalidException;
import com.pdonha.pix.domain.model.IdempotencyKey;
import com.pdonha.pix.domain.model.IdempotencyStatus;
import com.pdonha.pix.domain.model.Money;
import com.pdonha.pix.domain.model.TransferStatus;
import com.pdonha.pix.domain.port.IdempotencyKeyRepository;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("IdempotencyServiceOptimisticLockTest")
class IdempotencyServiceOptimisticLockTest {

    @Mock
    private IdempotencyKeyRepository idempotencyKeyRepository;

    @Mock
    private CreatePixTransferService createPixTransferService;

    private IdempotencyService idempotencyService;

    @BeforeEach
    void setUp() {
        idempotencyService = new IdempotencyService(idempotencyKeyRepository, createPixTransferService);
    }

    @Test
    @DisplayName("should retry on OptimisticLockException and succeed")
    void shouldRetryOnOptimisticLockAndSucceed() {
        CreatePixTransferCommand command = new CreatePixTransferCommand("key1", "key2", BigDecimal.TEN);
        TransferResult result = new TransferResult(
            UUID.randomUUID(),
            TransferStatus.COMPLETED,
            new Money(BigDecimal.TEN),
            LocalDateTime.now()
        );
        String idempotencyKey = "test-key-123";

        when(idempotencyKeyRepository.findByKey(idempotencyKey)).thenReturn(Optional.empty());
        doThrow(new jakarta.persistence.OptimisticLockException("Version mismatch"))
                .doThrow(new jakarta.persistence.OptimisticLockException("Version mismatch"))
                .doReturn(result)
                .when(createPixTransferService).execute(command);

        TransferResult actualResult = idempotencyService.executeWithIdempotency(idempotencyKey, command);

        assertEquals(result.getTransferId(), actualResult.getTransferId());
        verify(createPixTransferService, times(3)).execute(command);
    }

    @Test
    @DisplayName("should mark as FAILED after max retries exceeded")
    void shouldMarkAsFailedAfterMaxRetriesExceeded() {
        CreatePixTransferCommand command = new CreatePixTransferCommand("key1", "key2", BigDecimal.TEN);
        String idempotencyKey = "test-key-456";

        when(idempotencyKeyRepository.findByKey(idempotencyKey)).thenReturn(Optional.empty());
        doThrow(new jakarta.persistence.OptimisticLockException("Version mismatch"))
                .when(createPixTransferService).execute(command);

        assertThrows(jakarta.persistence.OptimisticLockException.class, () -> {
            idempotencyService.executeWithIdempotency(idempotencyKey, command);
        });

        verify(createPixTransferService, times(4)).execute(command);
    }

    @Test
    @DisplayName("should not retry on non-OptimisticLock exceptions")
    void shouldNotRetryOnNonOptimisticLockExceptions() {
        CreatePixTransferCommand command = new CreatePixTransferCommand("key1", "key2", BigDecimal.TEN);
        String idempotencyKey = "test-key-789";

        when(idempotencyKeyRepository.findByKey(idempotencyKey)).thenReturn(Optional.empty());
        doThrow(new RuntimeException("Some other error"))
                .when(createPixTransferService).execute(command);

        assertThrows(RuntimeException.class, () -> {
            idempotencyService.executeWithIdempotency(idempotencyKey, command);
        });

        verify(createPixTransferService, times(1)).execute(command);
    }

    @Test
    @DisplayName("should throw exception if idempotency key is null")
    void shouldThrowIfIdempotencyKeyIsNull() {
        CreatePixTransferCommand command = new CreatePixTransferCommand("key1", "key2", BigDecimal.TEN);

        assertThrows(IdempotencyKeyInvalidException.class, () -> {
            idempotencyService.executeWithIdempotency(null, command);
        });
    }
}
