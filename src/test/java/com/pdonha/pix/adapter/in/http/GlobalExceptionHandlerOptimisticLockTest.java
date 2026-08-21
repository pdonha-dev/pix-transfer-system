package com.pdonha.pix.adapter.in.http;

import com.pdonha.pix.domain.exception.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerOptimisticLockTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturnRfc7807ConflictForOptimisticLock() {
        ResponseEntity<ProblemDetail> response =
                handler.handleOptimisticLock(new OptimisticLockException("Version mismatch"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("OPTIMISTIC_LOCK_FAILED", response.getBody().getProperties().get("error_code"));
        assertEquals("/problems/optimistic-lock-failed", response.getBody().getType().toString());
    }
}
