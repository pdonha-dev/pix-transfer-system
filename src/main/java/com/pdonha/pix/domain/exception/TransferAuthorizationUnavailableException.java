package com.pdonha.pix.domain.exception;

public class TransferAuthorizationUnavailableException extends PixException {
    public TransferAuthorizationUnavailableException(String message) {
        super(message);
    }

    public TransferAuthorizationUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
