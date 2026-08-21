package com.pdonha.pix.application.service;

import com.pdonha.pix.application.dto.command.CreatePixTransferCommand;
import com.pdonha.pix.application.dto.result.TransferResult;
import com.pdonha.pix.domain.exception.IdempotencyKeyFailedException;
import com.pdonha.pix.domain.exception.IdempotencyKeyConflictException;
import com.pdonha.pix.domain.exception.IdempotencyKeyInvalidException;
import com.pdonha.pix.domain.exception.IdempotencyKeyStillProcessingException;
import com.pdonha.pix.domain.exception.RetryInterruptedException;
import com.pdonha.pix.domain.model.IdempotencyKey;
import com.pdonha.pix.domain.model.IdempotencyStatus;
import jakarta.persistence.OptimisticLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@Service
public class IdempotencyService {

    private final IdempotencyRecordService idempotencyRecordService;
    private final IdempotentTransferAttemptService transferAttemptService;
    private final CreatePixTransferService createPixTransferService;

    public IdempotencyService(IdempotencyRecordService idempotencyRecordService,
                             IdempotentTransferAttemptService transferAttemptService,
                             CreatePixTransferService createPixTransferService) {
        this.idempotencyRecordService = idempotencyRecordService;
        this.transferAttemptService = transferAttemptService;
        this.createPixTransferService = createPixTransferService;
    }

    private static final int MAX_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 10;

    public TransferResult executeWithIdempotency(String idempotencyKey, CreatePixTransferCommand command) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IdempotencyKeyInvalidException("Idempotency key cannot be blank or null");
        }

        String requestHash = requestHash(command);
        Optional<IdempotencyKey> existing = idempotencyRecordService.findByKey(idempotencyKey);

        if (existing.isPresent()) {
            return resolveExisting(existing.get(), requestHash);
        }

        UUID transferId = UUID.randomUUID();
        try {
            idempotencyRecordService.reserve(new IdempotencyKey(idempotencyKey, transferId, requestHash));
        } catch (DataIntegrityViolationException exception) {
            IdempotencyKey concurrentRecord = idempotencyRecordService.findByKey(idempotencyKey)
                    .orElseThrow(() -> exception);
            return resolveExisting(concurrentRecord, requestHash);
        }

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                return transferAttemptService.execute(idempotencyKey, transferId, command);
            } catch (RuntimeException exception) {
                if (!isOptimisticLock(exception) || attempt == MAX_ATTEMPTS) {
                    markFailedPreserving(idempotencyKey, exception);
                    throw exception;
                }
                try {
                    Thread.sleep(RETRY_DELAY_MS * (1L << (attempt - 1)));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    RetryInterruptedException interrupted =
                            new RetryInterruptedException("Retry interrupted", ie);
                    markFailedPreserving(idempotencyKey, interrupted);
                    throw interrupted;
                }
            }
        }
        throw new IllegalStateException("Retry loop completed without result");
    }

    private TransferResult resolveExisting(IdempotencyKey key, String requestHash) {
        if (!key.getRequestHash().startsWith("legacy:")
                && !key.getRequestHash().equals(requestHash)) {
            throw new IdempotencyKeyConflictException("Idempotency key was already used with a different request");
        }
        if (key.getStatus() == IdempotencyStatus.SUCCESS) {
            return createPixTransferService.getTransferResult(key.getTransferId());
        }
        if (key.getStatus() == IdempotencyStatus.PENDING) {
            throw new IdempotencyKeyStillProcessingException(
                    "Idempotency key still processing from previous request: " + key.getKey());
        }
        throw new IdempotencyKeyFailedException(
                "Idempotency key failed in previous attempt: " + key.getKey());
    }

    private boolean isOptimisticLock(RuntimeException exception) {
        return exception instanceof OptimisticLockException
                || exception instanceof OptimisticLockingFailureException;
    }

    private void markFailedPreserving(String idempotencyKey, RuntimeException original) {
        try {
            idempotencyRecordService.markFailed(idempotencyKey);
        } catch (RuntimeException statusFailure) {
            original.addSuppressed(statusFailure);
        }
    }

    static String requestHash(CreatePixTransferCommand command) {
        String canonical = command.getOriginPixKey() + "\n"
                + command.getDestinationPixKey() + "\n"
                + command.getAmount().stripTrailingZeros().toPlainString();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(canonical.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
