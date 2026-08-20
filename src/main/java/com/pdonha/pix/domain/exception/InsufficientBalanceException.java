package com.pdonha.pix.domain.exception;

/**
 * Thrown when attempting a withdrawal or transfer with insufficient account balance.
 */
public class InsufficientBalanceException extends PixException {
    public InsufficientBalanceException(String message) {
        super(message);
    }

    public InsufficientBalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
