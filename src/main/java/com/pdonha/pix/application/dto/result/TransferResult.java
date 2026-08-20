package com.pdonha.pix.application.dto.result;

import com.pdonha.pix.domain.model.Money;
import com.pdonha.pix.domain.model.TransferStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class TransferResult {
    private UUID transferId;
    private TransferStatus status;
    private Money amount;
    private LocalDateTime createdAt;

    public TransferResult(UUID transferId, TransferStatus status, Money amount, LocalDateTime createdAt) {
        this.transferId = transferId;
        this.status = status;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public UUID getTransferId() {
        return transferId;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public Money getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
