package com.pdonha.pix.domain.model;

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
            throw new IllegalArgumentException("Id não pode ser nulo");
        }
        if (accountId == null) {
            throw new IllegalArgumentException("AccountId não pode ser nulo");
        }
        if (type == null) {
            throw new IllegalArgumentException("Tipo da chave não pode ser nulo");
        }
        if (!type.isValid(value)) {
            throw new IllegalArgumentException("Valor da chave inválido para o tipo " + type);
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
            throw new IllegalArgumentException("Chave já está desativada");
        }
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void unblock() {
        if (active) {
            throw new IllegalArgumentException("Chave já está ativa");
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
