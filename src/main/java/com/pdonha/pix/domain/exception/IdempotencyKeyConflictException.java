package com.pdonha.pix.domain.exception;

public class IdempotencyKeyConflictException extends PixException {
    public IdempotencyKeyConflictException(String message) {
        super(message);
    }
}
