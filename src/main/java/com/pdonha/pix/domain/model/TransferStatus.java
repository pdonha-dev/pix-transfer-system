package com.pdonha.pix.domain.model;

public enum TransferStatus {
    PENDING,
    COMPLETED,
    FAILED,
    CANCELLED;

    public boolean canTransitionTo(TransferStatus target) {
        if (this == PENDING) {
            return target == COMPLETED || target == FAILED || target == CANCELLED;
        }
        return false;
    }
}
