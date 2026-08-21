package com.pdonha.pix.adapter.in.http;

import com.pdonha.pix.domain.exception.AccountNotFoundException;
import com.pdonha.pix.domain.exception.IdempotencyKeyFailedException;
import com.pdonha.pix.domain.exception.IdempotencyKeyInvalidException;
import com.pdonha.pix.domain.exception.IdempotencyKeyStillProcessingException;
import com.pdonha.pix.domain.exception.OptimisticLockException;
import com.pdonha.pix.domain.exception.PixKeyNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PixKeyNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePixKeyNotFound(PixKeyNotFoundException ex) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "PIX_KEY_NOT_FOUND",
                ex.getMessage()
        );
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAccountNotFound(AccountNotFoundException ex) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "ACCOUNT_NOT_FOUND",
                ex.getMessage()
        );
    }

    @ExceptionHandler(IdempotencyKeyInvalidException.class)
    public ResponseEntity<Map<String, Object>> handleIdempotencyKeyInvalid(IdempotencyKeyInvalidException ex) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "IDEMPOTENCY_KEY_INVALID",
                ex.getMessage()
        );
    }

    @ExceptionHandler(IdempotencyKeyStillProcessingException.class)
    public ResponseEntity<Map<String, Object>> handleIdempotencyKeyStillProcessing(IdempotencyKeyStillProcessingException ex) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "IDEMPOTENCY_KEY_STILL_PROCESSING",
                ex.getMessage()
        );
    }

    @ExceptionHandler(IdempotencyKeyFailedException.class)
    public ResponseEntity<Map<String, Object>> handleIdempotencyKeyFailed(IdempotencyKeyFailedException ex) {
        return buildErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "IDEMPOTENCY_KEY_FAILED",
                ex.getMessage()
        );
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<Map<String, Object>> handleOptimisticLock(OptimisticLockException ex) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "OPTIMISTIC_LOCK_FAILED",
                "Resource was modified concurrently. Please retry the operation."
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Invalid request payload"
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "An unexpected error occurred"
        );
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status,
            String errorCode,
            String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("error_code", errorCode);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());

        return ResponseEntity.status(status).body(response);
    }
}
