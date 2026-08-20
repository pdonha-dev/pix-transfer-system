package com.pdonha.pix.domain.exception;

public class DailyLimitExceededException extends PixException {
    public DailyLimitExceededException(String message) {
        super(message);
    }

    public DailyLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
