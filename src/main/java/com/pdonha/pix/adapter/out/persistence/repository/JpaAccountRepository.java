package com.pdonha.pix.adapter.out.persistence.repository;

import com.pdonha.pix.adapter.out.persistence.entity.AccountJpaEntity;
import com.pdonha.pix.adapter.out.persistence.converter.AccountConverter;
import com.pdonha.pix.domain.model.Account;
import com.pdonha.pix.domain.port.AccountRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

// Spring Data JPA Repository (internal)
interface SpringDataAccountRepository extends JpaRepository<AccountJpaEntity, UUID> {
}

// Domain Port Implementation
@Component
public class JpaAccountRepository implements AccountRepository {
    
    private final SpringDataAccountRepository springRepo;
    private final AccountConverter converter;
    
    public JpaAccountRepository(SpringDataAccountRepository springRepo, AccountConverter converter) {
        this.springRepo = springRepo;
        this.converter = converter;
    }
    
    @Override
    public Account findById(UUID id) {
        return springRepo.findById(id)
            .map(converter::toDomain)
            .orElse(null);
    }
    
    @Override
    public void save(Account account) {
        AccountJpaEntity entity = converter.toJpaEntity(account);
        AccountJpaEntity existing = springRepo.findById(account.getId()).orElse(null);
        
        if (existing != null) {
            entity.setVersion(existing.getVersion());
            entity = springRepo.saveAndFlush(entity);
        } else {
            springRepo.save(entity);
        }
    }
}
