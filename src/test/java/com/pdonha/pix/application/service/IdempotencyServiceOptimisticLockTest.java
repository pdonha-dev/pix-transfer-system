package com.pdonha.pix.application.service;

import com.pdonha.pix.application.dto.command.CreatePixTransferCommand;
import com.pdonha.pix.application.dto.result.TransferResult;
import com.pdonha.pix.domain.model.Money;
import com.pdonha.pix.domain.model.TransferStatus;
import jakarta.persistence.OptimisticLockException;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdempotencyServiceOptimisticLockTest {

    @Mock
    private IdempotencyRecordService recordService;
    @Mock
    private IdempotentTransferAttemptService attemptService;
    @Mock
    private CreatePixTransferService transferService;

    private IdempotencyService service;
    private CreatePixTransferCommand command;

    @BeforeEach
    void setUp() {
        service = new IdempotencyService(recordService, attemptService, transferService);
        command = new CreatePixTransferCommand("key1", "key2", BigDecimal.TEN);
    }

    @Test
    void shouldRetryAtomicAttemptAndSucceed() {
        String key = "retry-key";
        TransferResult result = new TransferResult(
                UUID.randomUUID(), TransferStatus.PENDING, new Money(BigDecimal.TEN), LocalDateTime.now());
        when(recordService.findByKey(key)).thenReturn(Optional.empty());
        when(attemptService.execute(eq(key), any(UUID.class), eq(command)))
                .thenThrow(new OptimisticLockException("Version mismatch"))
                .thenThrow(new OptimisticLockException("Version mismatch"))
                .thenReturn(result);

        TransferResult actual = service.executeWithIdempotency(key, command);

        assertEquals(result.getTransferId(), actual.getTransferId());
        verify(attemptService, times(3)).execute(eq(key), any(UUID.class), eq(command));
        verify(recordService, times(1)).findByKey(key);
        verify(recordService, never()).markFailed(any());
    }

    @Test
    void shouldFailAfterThreeAttempts() {
        String key = "exhausted-key";
        when(recordService.findByKey(key)).thenReturn(Optional.empty());
        when(attemptService.execute(eq(key), any(UUID.class), eq(command)))
                .thenThrow(new OptimisticLockException("Version mismatch"));

        assertThrows(OptimisticLockException.class,
                () -> service.executeWithIdempotency(key, command));

        verify(attemptService, times(3)).execute(eq(key), any(UUID.class), eq(command));
        verify(recordService).markFailed(key);
    }
}
