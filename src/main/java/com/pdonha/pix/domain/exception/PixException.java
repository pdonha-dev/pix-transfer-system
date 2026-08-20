package com.pdonha.pix.domain.exception;

public class PixException extends RuntimeException {
    public PixException(String message) {
        super(message);
    }

    public PixException(String message, Throwable cause) {
        super(message, cause);
    }
}
