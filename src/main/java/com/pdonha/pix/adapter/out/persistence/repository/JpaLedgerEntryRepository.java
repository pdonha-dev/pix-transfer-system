package com.pdonha.pix.adapter.out.persistence.repository;

import com.pdonha.pix.adapter.out.persistence.converter.LedgerEntryConverter;
import com.pdonha.pix.adapter.out.persistence.entity.LedgerEntryJpaEntity;
import com.pdonha.pix.domain.model.LedgerEntry;
import com.pdonha.pix.domain.port.LedgerEntryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

interface SpringDataLedgerEntryRepository extends JpaRepository<LedgerEntryJpaEntity, UUID> {
    List<LedgerEntryJpaEntity> findByTransferIdOrderByCreatedAtAsc(UUID transferId);
}

@Component
public class JpaLedgerEntryRepository implements LedgerEntryRepository {
    private final SpringDataLedgerEntryRepository repository;
    private final LedgerEntryConverter converter;

    public JpaLedgerEntryRepository(SpringDataLedgerEntryRepository repository,
                                    LedgerEntryConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public void save(LedgerEntry entry) {
        repository.saveAndFlush(converter.toJpaEntity(entry));
    }

    @Override
    public List<LedgerEntry> findByTransferId(UUID transferId) {
        return repository.findByTransferIdOrderByCreatedAtAsc(transferId)
                .stream()
                .map(converter::toDomain)
                .toList();
    }
}
