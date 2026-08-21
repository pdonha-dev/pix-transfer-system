package com.pdonha.pix.domain.exception;

public class InvalidEventStoreException extends PixException {
    public InvalidEventStoreException(String message) {
        super(message);
    }

    public InvalidEventStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
