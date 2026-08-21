package com.pdonha.pix.adapter.out.persistence.repository;

import com.pdonha.pix.adapter.out.persistence.entity.PixKeyJpaEntity;
import com.pdonha.pix.adapter.out.persistence.converter.PixKeyConverter;
import com.pdonha.pix.domain.model.PixKey;
import com.pdonha.pix.domain.port.PixKeyRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.Optional;

// Spring Data JPA Repository (internal)
interface SpringDataPixKeyRepository extends JpaRepository<PixKeyJpaEntity, UUID> {
    Optional<PixKeyJpaEntity> findByKeyValue(String keyValue);
}

// Domain Port Implementation
@Component
public class JpaPixKeyRepository implements PixKeyRepository {
    
    private final SpringDataPixKeyRepository springRepo;
    private final PixKeyConverter converter;
    
    public JpaPixKeyRepository(SpringDataPixKeyRepository springRepo, PixKeyConverter converter) {
        this.springRepo = springRepo;
        this.converter = converter;
    }
    
    @Override
    public PixKey findByKey(String key) {
        return springRepo.findByKeyValue(key)
            .map(converter::toDomain)
            .orElse(null);
    }
    
    @Override
    public void save(PixKey pixKey) {
        PixKeyJpaEntity entity = converter.toJpaEntity(pixKey);
        springRepo.saveAndFlush(entity);
    }
}
