package com.pdonha.pix.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("InvalidIdempotencyKeyException")
class InvalidIdempotencyKeyExceptionTest {

    @Test
    @DisplayName("should create exception with message")
    void shouldCreateWithMessage() {
        String message = "Idempotency key cannot be blank";
        InvalidIdempotencyKeyException exception = new InvalidIdempotencyKeyException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("should create exception with message and cause")
    void shouldCreateWithMessageAndCause() {
        String message = "Invalid idempotency key";
        Throwable cause = new IllegalArgumentException("Key is empty");
        InvalidIdempotencyKeyException exception = new InvalidIdempotencyKeyException(message, cause);

        assertEquals(message, exception.getMessage());
        assertNotNull(exception.getCause());
    }

    @Test
    @DisplayName("should extend PixException")
    void shouldExtendPixException() {
        InvalidIdempotencyKeyException exception = new InvalidIdempotencyKeyException("Test");
        org.junit.jupiter.api.Assertions.assertTrue(exception instanceof PixException);
    }
}
