package com.pdonha.pix.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("OptimisticLockException")
class OptimisticLockExceptionTest {

    @Test
    @DisplayName("should create with message")
    void shouldCreateWithMessage() {
        String message = "Version mismatch";
        OptimisticLockException ex = new OptimisticLockException(message);

        assertEquals(message, ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
    }

    @Test
    @DisplayName("should create with message and cause")
    void shouldCreateWithMessageAndCause() {
        String message = "Version mismatch";
        Throwable cause = new IllegalStateException("Concurrent modification");
        OptimisticLockException ex = new OptimisticLockException(message, cause);

        assertEquals(message, ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    @DisplayName("should be throwable")
    void shouldBeThrowable() {
        OptimisticLockException ex = new OptimisticLockException("Test");

        assertThrows(OptimisticLockException.class, () -> {
            throw ex;
        });
    }
}
