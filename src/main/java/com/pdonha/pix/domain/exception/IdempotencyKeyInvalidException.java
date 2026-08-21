package com.pdonha.pix.domain.exception;

public class IdempotencyKeyInvalidException extends PixException {
    public IdempotencyKeyInvalidException(String message) {
        super(message);
    }

    public IdempotencyKeyInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
