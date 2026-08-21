package com.pdonha.pix.domain.exception;

public class IdempotencyKeyStillProcessingException extends PixException {
    public IdempotencyKeyStillProcessingException(String message) {
        super(message);
    }

    public IdempotencyKeyStillProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
