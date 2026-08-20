package com.pdonha.pix.adapter.out.persistence.repository;

import com.pdonha.pix.adapter.out.persistence.entity.TransferJpaEntity;
import com.pdonha.pix.adapter.out.persistence.converter.TransferConverter;
import com.pdonha.pix.domain.model.Transfer;
import com.pdonha.pix.domain.port.TransferRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

// Spring Data JPA Repository (internal)
interface SpringDataTransferRepository extends JpaRepository<TransferJpaEntity, UUID> {
}

// Domain Port Implementation
@Component
public class JpaTransferRepository implements TransferRepository {
    
    private final SpringDataTransferRepository springRepo;
    private final TransferConverter converter;
    
    public JpaTransferRepository(SpringDataTransferRepository springRepo, TransferConverter converter) {
        this.springRepo = springRepo;
        this.converter = converter;
    }
    
    @Override
    public Transfer findById(UUID id) {
        return springRepo.findById(id)
            .map(converter::toDomain)
            .orElse(null);
    }
    
    @Override
    public void save(Transfer transfer) {
        TransferJpaEntity entity = converter.toJpaEntity(transfer);
        springRepo.save(entity);
    }
}
