package com.pdonha.pix.domain.exception;

/**
 * Base exception for Pix transfer system domain errors.
 * All specific domain exceptions extend this class.
 */
public class PixException extends RuntimeException {
    public PixException(String message) {
        super(message);
    }

    public PixException(String message, Throwable cause) {
        super(message, cause);
    }
}
