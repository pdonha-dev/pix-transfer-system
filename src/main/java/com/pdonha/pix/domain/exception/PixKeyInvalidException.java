package com.pdonha.pix.domain.exception;

/**
 * Thrown when a Pix key fails validation.
 */
public class PixKeyInvalidException extends PixException {
    public PixKeyInvalidException(String message) {
        super(message);
    }

    public PixKeyInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
