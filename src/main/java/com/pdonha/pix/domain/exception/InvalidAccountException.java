package com.pdonha.pix.domain.exception;

public class InvalidAccountException extends PixException {
    public InvalidAccountException(String message) {
        super(message);
    }

    public InvalidAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
