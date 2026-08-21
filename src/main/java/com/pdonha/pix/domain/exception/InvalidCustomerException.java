package com.pdonha.pix.domain.exception;

public class InvalidCustomerException extends PixException {
    public InvalidCustomerException(String message) {
        super(message);
    }

    public InvalidCustomerException(String message, Throwable cause) {
        super(message, cause);
    }
}
