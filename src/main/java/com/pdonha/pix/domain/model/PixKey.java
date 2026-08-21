package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.exception.InvalidPixKeyException;
import com.pdonha.pix.domain.exception.PixKeyInvalidException;

import java.time.LocalDateTime;
import java.util.UUID;

public class PixKey {
    private final UUID id;
    private final UUID accountId;
    private final PixKeyType type;
    private final String value;
    private boolean active;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PixKey(UUID id, UUID accountId, PixKeyType type, String value) {
        if (id == null) {
            throw new InvalidPixKeyException("Pix key ID cannot be null");
        }
        if (accountId == null) {
            throw new InvalidPixKeyException("Account ID cannot be null");
        }
        if (type == null) {
            throw new InvalidPixKeyException("Pix key type cannot be null");
        }
        if (!type.isValid(value)) {
            throw new PixKeyInvalidException("Pix key value does not match the specified type");
        }

        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.value = value;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public PixKeyType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void block() {
        if (!active) {
            throw new PixKeyInvalidException("Pix key is already blocked");
        }
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void unblock() {
        if (active) {
            throw new PixKeyInvalidException("Pix key is already active");
        }
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PixKey pixKey = (PixKey) obj;
        return id.equals(pixKey.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
