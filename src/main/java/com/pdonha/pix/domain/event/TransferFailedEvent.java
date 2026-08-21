package com.pdonha.pix.domain.event;

import java.util.UUID;

public class TransferFailedEvent extends DomainEvent {
    private final String failureReason;
    private final String failedBy;

    public TransferFailedEvent(UUID transferId, int version, String failureReason, String failedBy) {
        super(transferId, "Transfer", version);
        this.failureReason = failureReason;
        this.failedBy = failedBy;
    }

    @Override
    public String getEventType() {
        return "TransferFailed";
    }

    public String getFailureReason() {
        return failureReason;
    }

    public String getFailedBy() {
        return failedBy;
    }
}
