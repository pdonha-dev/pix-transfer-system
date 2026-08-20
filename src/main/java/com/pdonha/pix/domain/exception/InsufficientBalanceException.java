package com.pdonha.pix.domain.exception;

public class InsufficientBalanceException extends PixException {
    public InsufficientBalanceException(String message) {
        super(message);
    }

    public InsufficientBalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
