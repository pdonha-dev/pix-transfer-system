package com.pdonha.pix.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transfers")
public class TransferJpaEntity {
    
    @Id
    private UUID id;
    
    @Column(name = "payer_account_id", nullable = false)
    private UUID payerAccountId;
    
    @Column(name = "payee_account_id", nullable = false)
    private UUID payeeAccountId;
    
    @Column(name = "amount", nullable = false, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "status", nullable = false)
    private String status;
    
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
    
    public UUID getPayerAccountId() {
        return payerAccountId;
    }
    
    public void setPayerAccountId(UUID payerAccountId) {
        this.payerAccountId = payerAccountId;
    }
    
    public UUID getPayeeAccountId() {
        return payeeAccountId;
    }
    
    public void setPayeeAccountId(UUID payeeAccountId) {
        this.payeeAccountId = payeeAccountId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
