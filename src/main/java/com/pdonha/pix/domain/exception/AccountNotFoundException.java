package com.pdonha.pix.domain.exception;

public class AccountNotFoundException extends PixException {
    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
