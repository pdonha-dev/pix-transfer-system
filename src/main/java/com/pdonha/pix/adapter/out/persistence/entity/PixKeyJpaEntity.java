package com.pdonha.pix.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pix_keys")
public class PixKeyJpaEntity {
    
    @Id
    private UUID id;
    
    @Column(name = "account_id", nullable = false)
    private UUID accountId;
    
    @Column(name = "key_type", nullable = false)
    private String keyType;
    
    @Column(name = "key_value", nullable = false, unique = true)
    private String keyValue;
    
    @Column(name = "is_active", nullable = false)
    private boolean isActive;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getAccountId() {
        return accountId;
    }
    
    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }
    
    public String getKeyType() {
        return keyType;
    }
    
    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }
    
    public String getKeyValue() {
        return keyValue;
    }
    
    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
