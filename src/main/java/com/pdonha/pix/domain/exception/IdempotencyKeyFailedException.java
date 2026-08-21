package com.pdonha.pix.domain.exception;

public class IdempotencyKeyFailedException extends PixException {
    public IdempotencyKeyFailedException(String message) {
        super(message);
    }

    public IdempotencyKeyFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
