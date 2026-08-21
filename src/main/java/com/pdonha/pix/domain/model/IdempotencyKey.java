package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.exception.IdempotencyKeyInvalidException;
import com.pdonha.pix.domain.exception.InvalidIdempotencyKeyException;

import java.time.LocalDateTime;
import java.util.UUID;

public class IdempotencyKey {
    private final UUID id;
    private final String key;
    private final UUID transferId;
    private final String requestHash;
    private IdempotencyStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public IdempotencyKey(String key, UUID transferId, String requestHash) {
        this(UUID.randomUUID(), key, transferId, requestHash, IdempotencyStatus.PENDING,
                LocalDateTime.now(), LocalDateTime.now());
    }

    private IdempotencyKey(UUID id, String key, UUID transferId, String requestHash,
                           IdempotencyStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (id == null) {
            throw new InvalidIdempotencyKeyException("Idempotency ID cannot be null");
        }
        if (key == null || key.isBlank()) {
            throw new IdempotencyKeyInvalidException("Idempotency key cannot be blank");
        }
        if (transferId == null) {
            throw new InvalidIdempotencyKeyException("Transfer ID cannot be null");
        }
        if (status == null) {
            throw new InvalidIdempotencyKeyException("Idempotency status cannot be null");
        }
        if (requestHash == null || requestHash.isBlank()) {
            throw new InvalidIdempotencyKeyException("Request hash cannot be null or blank");
        }
        if (createdAt == null || updatedAt == null) {
            throw new InvalidIdempotencyKeyException("Idempotency timestamps cannot be null");
        }

        this.id = id;
        this.key = key;
        this.transferId = transferId;
        this.requestHash = requestHash;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static IdempotencyKey rehydrate(UUID id, String key, UUID transferId, String requestHash,
                                           IdempotencyStatus status, LocalDateTime createdAt,
                                           LocalDateTime updatedAt) {
        return new IdempotencyKey(id, key, transferId, requestHash, status, createdAt, updatedAt);
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

    public String getRequestHash() {
        return requestHash;
    }

    public IdempotencyStatus getStatus() {
        return status;
    }

    public void markSuccessful() {
        transitionTo(IdempotencyStatus.SUCCESS);
    }

    public void markFailed() {
        transitionTo(IdempotencyStatus.FAILED);
    }

    private void transitionTo(IdempotencyStatus newStatus) {
        if (status != IdempotencyStatus.PENDING) {
            throw new InvalidIdempotencyKeyException("Only pending idempotency keys can change status");
        }
        this.status = newStatus;
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
