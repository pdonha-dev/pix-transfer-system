package com.pdonha.pix.adapter.out.persistence.converter;

import com.pdonha.pix.adapter.out.persistence.entity.PixKeyJpaEntity;
import com.pdonha.pix.domain.model.PixKey;
import com.pdonha.pix.domain.model.PixKeyType;
import org.springframework.stereotype.Component;

@Component
public class PixKeyConverter {
    
    public PixKey toDomain(PixKeyJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return PixKey.rehydrate(
            entity.getId(),
            entity.getAccountId(),
            PixKeyType.valueOf(entity.getKeyType()),
            entity.getKeyValue(),
            entity.isActive(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
    
    public PixKeyJpaEntity toJpaEntity(PixKey domain) {
        if (domain == null) {
            return null;
        }
        
        PixKeyJpaEntity entity = new PixKeyJpaEntity();
        entity.setId(domain.getId());
        entity.setAccountId(domain.getAccountId());
        entity.setKeyType(domain.getType().name());
        entity.setKeyValue(domain.getValue());
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        
        return entity;
    }
}
