package com.pdonha.pix.domain.exception;

public class InvalidPixKeyException extends PixException {
    public InvalidPixKeyException(String message) {
        super(message);
    }

    public InvalidPixKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
