package com.pdonha.pix.adapter.in.http.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import com.pdonha.pix.domain.model.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response containing the created PIX transfer details")
public class CreatePixTransferResponse {
    @JsonProperty("transfer_id")
    @Schema(
        description = "Unique transfer identifier (UUID)",
        example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private UUID transferId;

    @Schema(
        description = "Current transfer status (PENDING, COMPLETED, FAILED, CANCELLED)",
        example = "PENDING"
    )
    private String status;

    @Schema(
        description = "Transfer amount in BRL (Real)",
        example = "100.00"
    )
    private BigDecimal amount;

    @JsonProperty("created_at")
    @Schema(
        description = "Transfer creation timestamp (ISO 8601)",
        example = "2026-08-20T20:00:00Z"
    )
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
