package com.pdonha.pix.adapter.in.http;

import com.pdonha.pix.domain.exception.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("GlobalExceptionHandlerOptimisticLockTest")
class GlobalExceptionHandlerOptimisticLockTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("should handle OptimisticLockException with 409 Conflict")
    void shouldHandleOptimisticLockException() {
        OptimisticLockException ex = new OptimisticLockException("Version mismatch");

        ResponseEntity<Map<String, Object>> response = handler.handleOptimisticLock(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("OPTIMISTIC_LOCK_FAILED", response.getBody().get("error_code"));
        assertEquals(409, response.getBody().get("status"));
    }

    @Test
    @DisplayName("should handle Hibernate OptimisticLockException with 409 Conflict")
    void shouldHandleHibernateOptimisticLockException() {
        jakarta.persistence.OptimisticLockException ex = new jakarta.persistence.OptimisticLockException("Version mismatch");

        ResponseEntity<Map<String, Object>> response = handler.handleOptimisticLock(new OptimisticLockException("Test", ex));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("OPTIMISTIC_LOCK_FAILED", response.getBody().get("error_code"));
        assertEquals(409, response.getBody().get("status"));
    }
}
