package com.pdonha.pix.domain.exception;

public class PixKeyNotFoundException extends PixException {
    public PixKeyNotFoundException(String message) {
        super(message);
    }

    public PixKeyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
