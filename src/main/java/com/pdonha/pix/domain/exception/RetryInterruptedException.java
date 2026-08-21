package com.pdonha.pix.domain.exception;

public class RetryInterruptedException extends PixException {
    public RetryInterruptedException(String message) {
        super(message);
    }

    public RetryInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
