package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.exception.InvalidTransferException;
import com.pdonha.pix.domain.exception.InvalidTransferStatusException;
import com.pdonha.pix.domain.event.DomainEvent;
import com.pdonha.pix.domain.event.TransferCreatedEvent;

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
    private final List<DomainEvent> pendingEvents;
    private final Long version;

    public Transfer(UUID id, UUID payerAccountId, UUID payeeAccountId, Money amount) {
        this(id, payerAccountId, payeeAccountId, amount, TransferStatus.PENDING,
                LocalDateTime.now(), LocalDateTime.now(), null, true);
    }

    private Transfer(UUID id, UUID payerAccountId, UUID payeeAccountId, Money amount,
                     TransferStatus status, LocalDateTime createdAt, LocalDateTime updatedAt,
                     Long version, boolean emitCreatedEvent) {
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
        if (status == null || createdAt == null || updatedAt == null) {
            throw new InvalidTransferException("Persisted transfer state cannot contain null fields");
        }

        this.id = id;
        this.payerAccountId = payerAccountId;
        this.payeeAccountId = payeeAccountId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.pendingEvents = new ArrayList<>();
        this.version = version;
        if (emitCreatedEvent) {
            pendingEvents.add(new TransferCreatedEvent(id, payerAccountId, payeeAccountId, amount, "system"));
        }
    }

    public static Transfer rehydrate(UUID id, UUID payerAccountId, UUID payeeAccountId, Money amount,
                                     TransferStatus status, LocalDateTime createdAt,
                                     LocalDateTime updatedAt, Long version) {
        return new Transfer(id, payerAccountId, payeeAccountId, amount, status, createdAt, updatedAt, version, false);
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

    public Long getVersion() {
        return version;
    }

    public List<DomainEvent> getPendingEvents() {
        return List.copyOf(pendingEvents);
    }

    public void addPendingEvent(DomainEvent event) {
        if (event == null) {
            throw new InvalidTransferException("Domain event cannot be null");
        }
        pendingEvents.add(event);
    }

    public List<DomainEvent> drainPendingEvents() {
        List<DomainEvent> drained = List.copyOf(pendingEvents);
        pendingEvents.clear();
        return drained;
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
