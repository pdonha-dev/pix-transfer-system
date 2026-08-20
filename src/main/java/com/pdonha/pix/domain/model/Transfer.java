package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.exception.InvalidTransferException;
import com.pdonha.pix.domain.exception.InvalidTransferStatusException;
import com.pdonha.pix.domain.exception.PixException;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transfer {

    private final UUID id;
    private final UUID payerAccountId;
    private final UUID payeeAccountId;
    private final Money amount;
    private TransferStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Transfer(UUID id, UUID payerAccountId, UUID payeeAccountId, Money amount) {
        if (id == null) {
            throw new PixException("Transfer ID cannot be null");
        }
        if (payerAccountId == null) {
            throw new PixException("Payer account ID cannot be null");
        }
        if (payeeAccountId == null) {
            throw new PixException("Payee account ID cannot be null");
        }
        if (amount == null) {
            throw new PixException("Transfer amount cannot be null");
        }
        if (payeeAccountId.equals(payerAccountId)) {
            throw new InvalidTransferException("Cannot transfer to the same account");
        }

        this.id = id;
        this.payerAccountId = payerAccountId;
        this.payeeAccountId = payeeAccountId;
        this.amount = amount;
        this.status = TransferStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getPayerAccountId() {
        return payerAccountId;
    }

    public UUID getPayeeAccountId() {
        return payeeAccountId;
    }

    public Money getAmount() {
        return amount;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void complete() {
        if (this.status != TransferStatus.PENDING) {
            throw new InvalidTransferStatusException("Transfer is not in pending state");
        }

        this.status = TransferStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void fail() {
        if (this.status != TransferStatus.PENDING) {
            throw new InvalidTransferStatusException("Transfer is not in pending state");
        }

        this.status = TransferStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status != TransferStatus.PENDING) {
            throw new InvalidTransferStatusException("Transfer is not in pending state");
        }

        this.status = TransferStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Transfer transfer = (Transfer) obj;
        return id.equals(transfer.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
