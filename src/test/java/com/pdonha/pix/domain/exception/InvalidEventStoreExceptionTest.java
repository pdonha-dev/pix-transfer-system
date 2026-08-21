package com.pdonha.pix.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("InvalidEventStoreException")
class InvalidEventStoreExceptionTest {

    @Test
    @DisplayName("should create exception with message")
    void shouldCreateWithMessage() {
        String message = "Event ID cannot be null";
        InvalidEventStoreException exception = new InvalidEventStoreException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("should create exception with message and cause")
    void shouldCreateWithMessageAndCause() {
        String message = "Invalid event store data";
        Throwable cause = new IllegalArgumentException("Event type blank");
        InvalidEventStoreException exception = new InvalidEventStoreException(message, cause);

        assertEquals(message, exception.getMessage());
        assertNotNull(exception.getCause());
    }

    @Test
    @DisplayName("should extend PixException")
    void shouldExtendPixException() {
        InvalidEventStoreException exception = new InvalidEventStoreException("Test");
        org.junit.jupiter.api.Assertions.assertTrue(exception instanceof PixException);
    }
}
