package com.pdonha.pix.domain.exception;

public class InvalidTransferException extends PixException {
    public InvalidTransferException(String message) {
        super(message);
    }

    public InvalidTransferException(String message, Throwable cause) {
        super(message, cause);
    }
}
