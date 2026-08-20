package com.pdonha.pix.domain.exception;

/**
 * Thrown when attempting an invalid state transition for a transfer.
 */
public class InvalidTransferStatusException extends PixException {
    public InvalidTransferStatusException(String message) {
        super(message);
    }

    public InvalidTransferStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
