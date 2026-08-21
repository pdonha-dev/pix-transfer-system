package com.pdonha.pix.application.service;

import com.pdonha.pix.application.dto.command.CreatePixTransferCommand;
import com.pdonha.pix.application.dto.result.TransferResult;
import com.pdonha.pix.domain.exception.IdempotencyKeyFailedException;
import com.pdonha.pix.domain.exception.IdempotencyKeyInvalidException;
import com.pdonha.pix.domain.exception.IdempotencyKeyStillProcessingException;
import com.pdonha.pix.domain.exception.RetryInterruptedException;
import com.pdonha.pix.domain.model.IdempotencyKey;
import com.pdonha.pix.domain.model.IdempotencyStatus;
import com.pdonha.pix.domain.port.IdempotencyKeyRepository;
import jakarta.persistence.OptimisticLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class IdempotencyService {

    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final CreatePixTransferService createPixTransferService;

    public IdempotencyService(IdempotencyKeyRepository idempotencyKeyRepository,
                             CreatePixTransferService createPixTransferService) {
        this.idempotencyKeyRepository = idempotencyKeyRepository;
        this.createPixTransferService = createPixTransferService;
    }

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 10;

    @Transactional
    public TransferResult executeWithIdempotency(String idempotencyKey, CreatePixTransferCommand command) {
        return executeWithRetry(idempotencyKey, command, 0);
    }

    private TransferResult executeWithRetry(String idempotencyKey, CreatePixTransferCommand command, int attempt) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IdempotencyKeyInvalidException("Idempotency key cannot be blank or null");
        }

        Optional<IdempotencyKey> existing = idempotencyKeyRepository.findByKey(idempotencyKey);

        if (existing.isPresent()) {
            IdempotencyKey key = existing.get();

            if (key.getStatus() == IdempotencyStatus.SUCCESS) {
                return createPixTransferService.getTransferResult(key.getTransferId());
            }

            if (key.getStatus() == IdempotencyStatus.PENDING) {
                throw new IdempotencyKeyStillProcessingException(
                        "Idempotency key still processing from previous request: " + idempotencyKey
                );
            }

            if (key.getStatus() == IdempotencyStatus.FAILED) {
                throw new IdempotencyKeyFailedException(
                        "Idempotency key failed in previous attempt: " + idempotencyKey
                );
            }
        }

        IdempotencyKey pendingKey = new IdempotencyKey(idempotencyKey, UUID.randomUUID(), IdempotencyStatus.PENDING);
        idempotencyKeyRepository.save(pendingKey);

        try {
            TransferResult result = createPixTransferService.execute(command);

            IdempotencyKey successKey = new IdempotencyKey(idempotencyKey, result.getTransferId(), IdempotencyStatus.SUCCESS);
            idempotencyKeyRepository.save(successKey);

            return result;
        } catch (OptimisticLockException e) {
            if (attempt < MAX_RETRIES) {
                try {
                    Thread.sleep(RETRY_DELAY_MS * (long) Math.pow(2, attempt));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RetryInterruptedException("Retry interrupted", ie);
                }
                return executeWithRetry(idempotencyKey, command, attempt + 1);
            }
            IdempotencyKey failedKey = new IdempotencyKey(idempotencyKey, UUID.randomUUID(), IdempotencyStatus.FAILED);
            idempotencyKeyRepository.save(failedKey);
            throw e;
        } catch (Exception e) {
            IdempotencyKey failedKey = new IdempotencyKey(idempotencyKey, UUID.randomUUID(), IdempotencyStatus.FAILED);
            idempotencyKeyRepository.save(failedKey);

            throw e;
        }
    }
}
