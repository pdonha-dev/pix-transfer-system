package com.pdonha.pix.adapter.in.http.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pdonha.pix.domain.model.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class CreatePixTransferResponse {
    @JsonProperty("transfer_id")
    private UUID transferId;

    private String status;

    private BigDecimal amount;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public CreatePixTransferResponse() {}

    public CreatePixTransferResponse(UUID transferId, TransferStatus status, BigDecimal amount, LocalDateTime createdAt) {
        this.transferId = transferId;
        this.status = status.toString();
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public UUID getTransferId() {
        return transferId;
    }

    public void setTransferId(UUID transferId) {
        this.transferId = transferId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
