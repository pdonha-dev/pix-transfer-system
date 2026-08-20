package com.pdonha.pix.domain.exception;

/**
 * Thrown when a daily transfer limit has been exceeded.
 */
public class DailyLimitExceededException extends PixException {
    public DailyLimitExceededException(String message) {
        super(message);
    }

    public DailyLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
