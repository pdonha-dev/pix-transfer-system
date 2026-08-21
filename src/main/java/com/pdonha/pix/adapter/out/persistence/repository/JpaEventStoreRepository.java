package com.pdonha.pix.adapter.out.persistence.repository;

import com.pdonha.pix.adapter.out.persistence.entity.EventStoreJpaEntity;
import com.pdonha.pix.adapter.out.persistence.converter.EventStoreConverter;
import com.pdonha.pix.domain.model.EventStore;
import com.pdonha.pix.domain.port.EventStoreRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

interface SpringDataEventStoreRepository extends JpaRepository<EventStoreJpaEntity, UUID> {
    List<EventStoreJpaEntity> findByAggregateIdOrderByAggregateVersionAsc(UUID aggregateId);
    List<EventStoreJpaEntity> findByAggregateIdAndAggregateTypeOrderByAggregateVersionAsc(UUID aggregateId, String aggregateType);
}

@Component
public class JpaEventStoreRepository implements EventStoreRepository {

    private final SpringDataEventStoreRepository springRepo;
    private final EventStoreConverter converter;

    public JpaEventStoreRepository(SpringDataEventStoreRepository springRepo, EventStoreConverter converter) {
        this.springRepo = springRepo;
        this.converter = converter;
    }

    @Override
    public void save(EventStore eventStore) {
        EventStoreJpaEntity entity = converter.toJpaEntity(eventStore);
        springRepo.save(entity);
    }

    @Override
    public List<EventStore> findByAggregateId(UUID aggregateId) {
        return springRepo.findByAggregateIdOrderByAggregateVersionAsc(aggregateId)
                .stream()
                .map(converter::toDomain)
                .toList();
    }

    @Override
    public List<EventStore> findByAggregateIdAndType(UUID aggregateId, String aggregateType) {
        return springRepo.findByAggregateIdAndAggregateTypeOrderByAggregateVersionAsc(aggregateId, aggregateType)
                .stream()
                .map(converter::toDomain)
                .toList();
    }
}
