package com.pdonha.pix.adapter.in.http;

import com.pdonha.pix.domain.exception.AccountBlockedException;
import com.pdonha.pix.domain.exception.AccountNotFoundException;
import com.pdonha.pix.domain.exception.DailyLimitExceededException;
import com.pdonha.pix.domain.exception.IdempotencyKeyConflictException;
import com.pdonha.pix.domain.exception.IdempotencyKeyFailedException;
import com.pdonha.pix.domain.exception.IdempotencyKeyInvalidException;
import com.pdonha.pix.domain.exception.IdempotencyKeyStillProcessingException;
import com.pdonha.pix.domain.exception.InsufficientBalanceException;
import com.pdonha.pix.domain.exception.OptimisticLockException;
import com.pdonha.pix.domain.exception.PixKeyNotFoundException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PixKeyNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlePixKeyNotFound(PixKeyNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, "PIX_KEY_NOT_FOUND", "PIX key not found", exception.getMessage());
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleAccountNotFound(AccountNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND", "Account not found", exception.getMessage());
    }

    @ExceptionHandler(IdempotencyKeyInvalidException.class)
    public ResponseEntity<ProblemDetail> handleIdempotencyKeyInvalid(IdempotencyKeyInvalidException exception) {
        return problem(HttpStatus.BAD_REQUEST, "IDEMPOTENCY_KEY_INVALID",
                "Invalid idempotency key", exception.getMessage());
    }

    @ExceptionHandler({IdempotencyKeyStillProcessingException.class, IdempotencyKeyConflictException.class})
    public ResponseEntity<ProblemDetail> handleIdempotencyConflict(RuntimeException exception) {
        String code = exception instanceof IdempotencyKeyConflictException
                ? "IDEMPOTENCY_KEY_CONFLICT"
                : "IDEMPOTENCY_KEY_STILL_PROCESSING";
        return problem(HttpStatus.CONFLICT, code, "Idempotency conflict", exception.getMessage());
    }

    @ExceptionHandler(IdempotencyKeyFailedException.class)
    public ResponseEntity<ProblemDetail> handleIdempotencyKeyFailed(IdempotencyKeyFailedException exception) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "IDEMPOTENCY_KEY_FAILED",
                "Previous request failed", exception.getMessage());
    }

    @ExceptionHandler({InsufficientBalanceException.class, DailyLimitExceededException.class})
    public ResponseEntity<ProblemDetail> handleBusinessRule(RuntimeException exception) {
        String code = exception instanceof InsufficientBalanceException
                ? "INSUFFICIENT_BALANCE"
                : "DAILY_LIMIT_EXCEEDED";
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, code,
                "Transfer cannot be processed", exception.getMessage());
    }

    @ExceptionHandler(AccountBlockedException.class)
    public ResponseEntity<ProblemDetail> handleAccountBlocked(AccountBlockedException exception) {
        return problem(HttpStatus.CONFLICT, "ACCOUNT_BLOCKED", "Account is blocked", exception.getMessage());
    }

    @ExceptionHandler({
            OptimisticLockException.class,
            jakarta.persistence.OptimisticLockException.class,
            OptimisticLockingFailureException.class
    })
    public ResponseEntity<ProblemDetail> handleOptimisticLock(RuntimeException exception) {
        return problem(HttpStatus.CONFLICT, "OPTIMISTIC_LOCK_FAILED", "Concurrent update",
                "Resource was modified concurrently. Please retry the operation.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException exception) {
        return problem(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Invalid request",
                "Request payload failed validation");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception exception) {
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Internal server error",
                "An unexpected error occurred");
    }

    private ResponseEntity<ProblemDetail> problem(HttpStatus status, String code,
                                                  String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create("/problems/" + code.toLowerCase().replace('_', '-')));
        problem.setProperty("error_code", code);
        return ResponseEntity.status(status).body(problem);
    }
}
