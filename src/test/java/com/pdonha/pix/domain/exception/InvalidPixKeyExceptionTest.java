package com.pdonha.pix.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("InvalidPixKeyException")
class InvalidPixKeyExceptionTest {

    @Test
    @DisplayName("should create exception with message")
    void shouldCreateWithMessage() {
        String message = "Pix key value cannot be empty";
        InvalidPixKeyException exception = new InvalidPixKeyException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("should create exception with message and cause")
    void shouldCreateWithMessageAndCause() {
        String message = "Invalid pix key";
        Throwable cause = new IllegalArgumentException("Key format incorrect");
        InvalidPixKeyException exception = new InvalidPixKeyException(message, cause);

        assertEquals(message, exception.getMessage());
        assertNotNull(exception.getCause());
    }

    @Test
    @DisplayName("should extend PixException")
    void shouldExtendPixException() {
        InvalidPixKeyException exception = new InvalidPixKeyException("Test");
        org.junit.jupiter.api.Assertions.assertTrue(exception instanceof PixException);
    }
}
