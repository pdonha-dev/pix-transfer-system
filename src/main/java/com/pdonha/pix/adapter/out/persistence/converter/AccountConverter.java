package com.pdonha.pix.adapter.out.persistence.converter;

import com.pdonha.pix.adapter.out.persistence.entity.AccountJpaEntity;
import com.pdonha.pix.domain.model.Account;
import com.pdonha.pix.domain.model.Money;
import org.springframework.stereotype.Component;

@Component
public class AccountConverter {
    
    public Account toDomain(AccountJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Account.rehydrate(
            entity.getId(),
            entity.getCustomerId(),
            new Money(entity.getBalance()),
            new Money(entity.getDailyLimit()),
            new Money(entity.getDailyUsed()),
            entity.isActive(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getVersion()
        );
    }
    
    public AccountJpaEntity toJpaEntity(Account domain) {
        if (domain == null) {
            return null;
        }
        
        AccountJpaEntity entity = new AccountJpaEntity();
        entity.setId(domain.getId());
        entity.setCustomerId(domain.getCustomerId());
        entity.setBalance(domain.getBalance().getAmount());
        entity.setDailyLimit(domain.getDailyLimit().getAmount());
        entity.setDailyUsed(domain.getDailyUsed().getAmount());
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setVersion(domain.getVersion());
        
        return entity;
    }
}
