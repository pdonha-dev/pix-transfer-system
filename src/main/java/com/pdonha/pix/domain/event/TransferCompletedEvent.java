package com.pdonha.pix.domain.event;

import java.util.UUID;

public class TransferCompletedEvent extends DomainEvent {
    private final String completedBy;

    public TransferCompletedEvent(UUID transferId, int version, String completedBy) {
        super(transferId, "Transfer", version);
        this.completedBy = completedBy;
    }

    @Override
    public String getEventType() {
        return "TransferCompleted";
    }

    public String getCompletedBy() {
        return completedBy;
    }
}
