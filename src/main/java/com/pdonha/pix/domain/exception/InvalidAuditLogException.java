package com.pdonha.pix.domain.exception;

public class InvalidAuditLogException extends PixException {
    public InvalidAuditLogException(String message) {
        super(message);
    }

    public InvalidAuditLogException(String message, Throwable cause) {
        super(message, cause);
    }
}
