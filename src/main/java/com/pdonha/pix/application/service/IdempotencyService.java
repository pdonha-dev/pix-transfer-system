package com.pdonha.pix.application.service;

import com.pdonha.pix.application.dto.command.CreatePixTransferCommand;
import com.pdonha.pix.application.dto.result.TransferResult;
import com.pdonha.pix.domain.exception.IdempotencyKeyFailedException;
import com.pdonha.pix.domain.exception.IdempotencyKeyInvalidException;
import com.pdonha.pix.domain.exception.IdempotencyKeyStillProcessingException;
import com.pdonha.pix.domain.model.IdempotencyKey;
import com.pdonha.pix.domain.model.IdempotencyStatus;
import com.pdonha.pix.domain.port.IdempotencyKeyRepository;
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

    @Transactional
    public TransferResult executeWithIdempotency(String idempotencyKey, CreatePixTransferCommand command) {
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
        } catch (Exception e) {
            IdempotencyKey failedKey = new IdempotencyKey(idempotencyKey, UUID.randomUUID(), IdempotencyStatus.FAILED);
            idempotencyKeyRepository.save(failedKey);

            throw e;
        }
    }
}
