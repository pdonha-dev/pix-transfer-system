package com.pdonha.pix.domain.exception;

/**
 * Thrown when attempting to perform a transfer to the same account.
 */
public class InvalidTransferException extends PixException {
    public InvalidTransferException(String message) {
        super(message);
    }

    public InvalidTransferException(String message, Throwable cause) {
        super(message, cause);
    }
}
