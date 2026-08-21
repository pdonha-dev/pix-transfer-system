package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.exception.IdempotencyKeyInvalidException;
import com.pdonha.pix.domain.exception.PixException;

import java.time.LocalDateTime;
import java.util.UUID;

public class IdempotencyKey {
    private final UUID id;
    private final String key;
    private final UUID transferId;
    private IdempotencyStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public IdempotencyKey(String key, UUID transferId, IdempotencyStatus status) {
        if (key == null || key.isBlank()) {
            throw new IdempotencyKeyInvalidException("Idempotency key cannot be blank");
        }
        if (transferId == null) {
            throw new PixException("Transfer ID cannot be null");
        }
        if (status == null) {
            throw new PixException("Idempotency status cannot be null");
        }

        this.id = UUID.randomUUID();
        this.key = key;
        this.transferId = transferId;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public UUID getTransferId() {
        return transferId;
    }

    public IdempotencyStatus getStatus() {
        return status;
    }

    public void setStatus(IdempotencyStatus status) {
        if (status == null) {
            throw new PixException("Idempotency status cannot be null");
        }
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        IdempotencyKey other = (IdempotencyKey) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
