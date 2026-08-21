package com.pdonha.pix.application.service;

import com.pdonha.pix.domain.exception.InvalidIdempotencyKeyException;
import com.pdonha.pix.domain.exception.IdempotencyKeyStillProcessingException;
import com.pdonha.pix.domain.model.IdempotencyKey;
import com.pdonha.pix.domain.port.IdempotencyKeyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class IdempotencyRecordService {

    private final IdempotencyKeyRepository repository;

    public IdempotencyRecordService(IdempotencyKeyRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Optional<IdempotencyKey> findByKey(String key) {
        return repository.findByKey(key);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void reserve(IdempotencyKey idempotencyKey) {
        repository.save(idempotencyKey);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(String key) {
        IdempotencyKey record = repository.findByKeyForUpdate(key)
                .orElseThrow(() -> new InvalidIdempotencyKeyException("Idempotency key not found: " + key));
        if (record.getStatus() == com.pdonha.pix.domain.model.IdempotencyStatus.PENDING) {
            record.markFailed();
            repository.save(record);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markRetryable(String key) {
        IdempotencyKey record = repository.findByKeyForUpdate(key)
                .orElseThrow(() -> new InvalidIdempotencyKeyException("Idempotency key not found: " + key));
        if (record.getStatus() == com.pdonha.pix.domain.model.IdempotencyStatus.PENDING) {
            record.markRetryable();
            repository.save(record);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void resume(String key) {
        IdempotencyKey record = repository.findByKeyForUpdate(key)
                .orElseThrow(() -> new InvalidIdempotencyKeyException("Idempotency key not found: " + key));
        if (record.getStatus() != com.pdonha.pix.domain.model.IdempotencyStatus.RETRYABLE) {
            throw new IdempotencyKeyStillProcessingException(
                    "Idempotency key is already being processed: " + key);
        }
        record.resumeProcessing();
        repository.save(record);
    }
}
