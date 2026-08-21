package com.pdonha.pix.adapter.out.persistence.converter;

import com.pdonha.pix.adapter.out.persistence.entity.TransferJpaEntity;
import com.pdonha.pix.domain.model.Money;
import com.pdonha.pix.domain.model.Transfer;
import com.pdonha.pix.domain.model.TransferStatus;
import org.springframework.stereotype.Component;

@Component
public class TransferConverter {
    
    public Transfer toDomain(TransferJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Transfer transfer = new Transfer(
            entity.getId(),
            entity.getPayerAccountId(),
            entity.getPayeeAccountId(),
            new Money(entity.getAmount())
        );

        return transfer;
    }
    
    public TransferJpaEntity toJpaEntity(Transfer domain) {
        if (domain == null) {
            return null;
        }
        
        TransferJpaEntity entity = new TransferJpaEntity();
        entity.setId(domain.getId());
        entity.setPayerAccountId(domain.getPayerAccountId());
        entity.setPayeeAccountId(domain.getPayeeAccountId());
        entity.setAmount(domain.getAmount().getAmount());
        entity.setStatus(domain.getStatus().name());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        
        return entity;
    }
}

