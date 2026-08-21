package com.pdonha.pix.adapter.out.persistence.converter;

import com.pdonha.pix.adapter.out.persistence.entity.IdempotencyKeyJpaEntity;
import com.pdonha.pix.domain.model.IdempotencyKey;
import com.pdonha.pix.domain.model.IdempotencyStatus;
import org.springframework.stereotype.Component;

@Component
public class IdempotencyKeyConverter {

    public IdempotencyKey toDomain(IdempotencyKeyJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        IdempotencyStatus status = IdempotencyStatus.valueOf(entity.getStatus().name());
        IdempotencyKey domain = new IdempotencyKey(entity.getKey(), entity.getTransferId(), status);

        // Reflect fields (since constructor creates new timestamps)
        return domain;
    }

    public IdempotencyKeyJpaEntity toJpaEntity(IdempotencyKey domain) {
        if (domain == null) {
            return null;
        }

        IdempotencyKeyJpaEntity entity = new IdempotencyKeyJpaEntity();
        entity.setId(domain.getId());
        entity.setKey(domain.getKey());
        entity.setTransferId(domain.getTransferId());
        entity.setStatus(IdempotencyKeyJpaEntity.IdempotencyStatusJpa.valueOf(domain.getStatus().name()));
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        return entity;
    }
}
