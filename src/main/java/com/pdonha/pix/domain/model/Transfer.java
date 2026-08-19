package com.pdonha.pix.domain.model;

import java.math.BigDecimal;
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
            throw new IllegalArgumentException("Id não pode ser nulo");
        }
        if (payerAccountId == null) {
            throw new IllegalArgumentException("PayerAccountId não pode ser nulo");
        }
        if (payeeAccountId == null) {
            throw new IllegalArgumentException("PayeeAccountId não pode ser nulo");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount não pode ser nulo");
        }
        if (payeeAccountId.equals(payerAccountId)) {
            throw new IllegalArgumentException("PayerAccountId e PayeeAccountId não podem ser iguais");
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
            throw new IllegalStateException("Transfer já está " + this.status);
        }

        this.status = TransferStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void fail() {
        if (this.status != TransferStatus.PENDING) {
            throw new IllegalStateException("Transfer já está " + this.status);
        }

        this.status = TransferStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status != TransferStatus.PENDING) {
            throw new IllegalStateException("Transfer já está " + this.status);
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
