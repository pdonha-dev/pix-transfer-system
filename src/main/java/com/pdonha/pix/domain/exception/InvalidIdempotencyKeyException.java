package com.pdonha.pix.domain.exception;

public class InvalidIdempotencyKeyException extends PixException {
    public InvalidIdempotencyKeyException(String message) {
        super(message);
    }

    public InvalidIdempotencyKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
