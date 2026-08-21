package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.exception.InvalidTransferException;
import com.pdonha.pix.domain.exception.InvalidTransferStatusException;
import com.pdonha.pix.domain.exception.PixException;
import com.pdonha.pix.domain.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Transfer {

    private final UUID id;
    private final UUID payerAccountId;
    private final UUID payeeAccountId;
    private final Money amount;
    private TransferStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<DomainEvent> events;

    public Transfer(UUID id, UUID payerAccountId, UUID payeeAccountId, Money amount) {
        if (id == null) {
            throw new InvalidTransferException("Transfer ID cannot be null");
        }
        if (payerAccountId == null) {
            throw new InvalidTransferException("Payer account ID cannot be null");
        }
        if (payeeAccountId == null) {
            throw new InvalidTransferException("Payee account ID cannot be null");
        }
        if (amount == null) {
            throw new InvalidTransferException("Transfer amount cannot be null");
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
        this.events = new ArrayList<>();
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

    public List<DomainEvent> getEvents() {
        return new ArrayList<>(events);
    }

    public void addEvent(DomainEvent event) {
        this.events.add(event);
    }

    public void clearEvents() {
        this.events.clear();
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
