package com.pdonha.pix.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("InvalidAuditLogException")
class InvalidAuditLogExceptionTest {

    @Test
    @DisplayName("should create exception with message")
    void shouldCreateWithMessage() {
        String message = "Entity type cannot be blank";
        InvalidAuditLogException exception = new InvalidAuditLogException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("should create exception with message and cause")
    void shouldCreateWithMessageAndCause() {
        String message = "Invalid audit log data";
        Throwable cause = new IllegalArgumentException("Changed by is empty");
        InvalidAuditLogException exception = new InvalidAuditLogException(message, cause);

        assertEquals(message, exception.getMessage());
        assertNotNull(exception.getCause());
    }

    @Test
    @DisplayName("should extend PixException")
    void shouldExtendPixException() {
        InvalidAuditLogException exception = new InvalidAuditLogException("Test");
        org.junit.jupiter.api.Assertions.assertTrue(exception instanceof PixException);
    }
}
