package com.pdonha.pix.adapter.out.persistence.repository;

import com.pdonha.pix.adapter.out.persistence.entity.IdempotencyKeyJpaEntity;
import com.pdonha.pix.adapter.out.persistence.converter.IdempotencyKeyConverter;
import com.pdonha.pix.domain.model.IdempotencyKey;
import com.pdonha.pix.domain.port.IdempotencyKeyRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

interface SpringDataIdempotencyKeyRepository extends JpaRepository<IdempotencyKeyJpaEntity, UUID> {
    Optional<IdempotencyKeyJpaEntity> findByKey(String key);

    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select key from IdempotencyKeyJpaEntity key where key.key = :key")
    Optional<IdempotencyKeyJpaEntity> findByKeyForUpdate(@Param("key") String key);
}

@Component
public class JpaIdempotencyKeyRepository implements IdempotencyKeyRepository {

    private final SpringDataIdempotencyKeyRepository springRepo;
    private final IdempotencyKeyConverter converter;

    public JpaIdempotencyKeyRepository(SpringDataIdempotencyKeyRepository springRepo, IdempotencyKeyConverter converter) {
        this.springRepo = springRepo;
        this.converter = converter;
    }

    @Override
    public Optional<IdempotencyKey> findByKey(String key) {
        return springRepo.findByKey(key)
                .map(converter::toDomain);
    }

    @Override
    public Optional<IdempotencyKey> findByKeyForUpdate(String key) {
        return springRepo.findByKeyForUpdate(key)
                .map(converter::toDomain);
    }

    @Override
    public void save(IdempotencyKey idempotencyKey) {
        IdempotencyKeyJpaEntity entity = converter.toJpaEntity(idempotencyKey);
        springRepo.saveAndFlush(entity);
    }
}
