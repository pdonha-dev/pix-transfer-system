package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.exception.InvalidTransferException;

public record TransferAuthorizationDecision(boolean authorized, String authorizationCode) {
    public TransferAuthorizationDecision {
        if (authorized && (authorizationCode == null || authorizationCode.isBlank())) {
            throw new InvalidTransferException("Approved authorization requires a code");
        }
    }

    public static TransferAuthorizationDecision approved(String authorizationCode) {
        return new TransferAuthorizationDecision(true, authorizationCode);
    }

    public static TransferAuthorizationDecision denied() {
        return new TransferAuthorizationDecision(false, null);
    }
}
