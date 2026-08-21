package com.pdonha.pix.adapter.out.persistence.converter;

import com.pdonha.pix.adapter.out.persistence.entity.LedgerEntryJpaEntity;
import com.pdonha.pix.domain.model.LedgerEntry;
import com.pdonha.pix.domain.model.LedgerEntryType;
import com.pdonha.pix.domain.model.Money;
import org.springframework.stereotype.Component;

@Component
public class LedgerEntryConverter {
    public LedgerEntryJpaEntity toJpaEntity(LedgerEntry domain) {
        LedgerEntryJpaEntity entity = new LedgerEntryJpaEntity();
        entity.setId(domain.getId());
        entity.setTransferId(domain.getTransferId());
        entity.setAccountId(domain.getAccountId());
        entity.setEntryType(domain.getType().name());
        entity.setAmount(domain.getAmount().getAmount());
        entity.setBalanceAfter(domain.getBalanceAfter().getAmount());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    public LedgerEntry toDomain(LedgerEntryJpaEntity entity) {
        return new LedgerEntry(
                entity.getId(),
                entity.getTransferId(),
                entity.getAccountId(),
                LedgerEntryType.valueOf(entity.getEntryType()),
                new Money(entity.getAmount()),
                new Money(entity.getBalanceAfter()),
                entity.getCreatedAt()
        );
    }
}
