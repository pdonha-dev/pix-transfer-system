package com.pdonha.pix.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("InvalidAccountException")
class InvalidAccountExceptionTest {

    @Test
    @DisplayName("should create exception with message")
    void shouldCreateWithMessage() {
        String message = "Account ID cannot be null";
        InvalidAccountException exception = new InvalidAccountException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("should create exception with message and cause")
    void shouldCreateWithMessageAndCause() {
        String message = "Invalid account balance";
        Throwable cause = new IllegalArgumentException("Negative balance");
        InvalidAccountException exception = new InvalidAccountException(message, cause);

        assertEquals(message, exception.getMessage());
        assertNotNull(exception.getCause());
    }

    @Test
    @DisplayName("should extend PixException")
    void shouldExtendPixException() {
        InvalidAccountException exception = new InvalidAccountException("Test");
        org.junit.jupiter.api.Assertions.assertTrue(exception instanceof PixException);
    }
}
