package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.exception.InvalidTransferStatusException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransferTest {

    private Transfer createTransfer() {
        return new Transfer(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                new Money(new BigDecimal("100.00")));
    }

    @Test
    void complete_shouldChangeStatusToCompleted_whenStatusIsPending() {
        // Arrange
        Transfer transfer = createTransfer();

        // Act
        transfer.complete();

        // Assert
        assertEquals(TransferStatus.COMPLETED, transfer.getStatus());
    }

    @Test
    void complete_shouldThrowInvalidTransferStatusException_whenStatusIsCompleted() {
        // Arrange
        Transfer transfer = createTransfer();
        transfer.complete();

        // Act & Assert
        assertThrows(InvalidTransferStatusException.class, transfer::complete);
    }

    @Test
    void complete_shouldThrowInvalidTransferStatusException_whenStatusIsFailed() {
        // Arrange
        Transfer transfer = createTransfer();
        transfer.fail();

        // Act & Assert
        assertThrows(InvalidTransferStatusException.class, transfer::complete);
    }

    @Test
    void fail_shouldChangeStatusToFailed_whenStatusIsPending() {
        // Arrange
        Transfer transfer = createTransfer();

        // Act
        transfer.fail();

        // Assert
        assertEquals(TransferStatus.FAILED, transfer.getStatus());
    }

    @Test
    void fail_shouldThrowInvalidTransferStatusException_whenStatusIsCompleted() {
        // Arrange
        Transfer transfer = createTransfer();
        transfer.complete();

        // Act & Assert
        assertThrows(InvalidTransferStatusException.class, transfer::fail);
    }

    @Test
    void fail_shouldThrowInvalidTransferStatusException_whenStatusIsFailed() {
        // Arrange
        Transfer transfer = createTransfer();
        transfer.fail();

        // Act & Assert
        assertThrows(InvalidTransferStatusException.class, transfer::fail);
    }

    @Test
    void cancel_shouldChangeStatusToCancelled_whenStatusIsPending() {
        // Arrange
        Transfer transfer = createTransfer();

        // Act
        transfer.cancel();

        // Assert
        assertEquals(TransferStatus.CANCELLED, transfer.getStatus());
    }

    @Test
    void cancel_shouldThrowInvalidTransferStatusException_whenStatusIsCompleted() {
        // Arrange
        Transfer transfer = createTransfer();
        transfer.complete();

        // Act & Assert
        assertThrows(InvalidTransferStatusException.class, transfer::cancel);
    }

    @Test
    void cancel_shouldThrowInvalidTransferStatusException_whenStatusIsFailed() {
        // Arrange
        Transfer transfer = createTransfer();
        transfer.fail();

        // Act & Assert
        assertThrows(InvalidTransferStatusException.class, transfer::cancel);
    }
}
