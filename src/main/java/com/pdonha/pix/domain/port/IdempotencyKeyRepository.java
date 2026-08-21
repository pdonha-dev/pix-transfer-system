package com.pdonha.pix.domain.port;

import com.pdonha.pix.domain.model.IdempotencyKey;

import java.util.Optional;

public interface IdempotencyKeyRepository {
    Optional<IdempotencyKey> findByKey(String key);
    void save(IdempotencyKey idempotencyKey);
}
