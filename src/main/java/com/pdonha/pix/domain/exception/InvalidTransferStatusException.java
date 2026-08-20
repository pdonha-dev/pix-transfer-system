package com.pdonha.pix.domain.exception;

public class InvalidTransferStatusException extends PixException {
    public InvalidTransferStatusException(String message) {
        super(message);
    }

    public InvalidTransferStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
