package com.pdonha.pix.adapter.out.persistence.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdonha.pix.adapter.out.persistence.entity.EventStoreJpaEntity;
import com.pdonha.pix.adapter.out.persistence.converter.EventStoreConverter;
import com.pdonha.pix.adapter.out.persistence.exception.EventSerializationException;
import com.pdonha.pix.domain.event.DomainEvent;
import com.pdonha.pix.domain.model.EventStore;
import com.pdonha.pix.domain.port.DomainEventRepository;
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
public class JpaEventStoreRepository implements EventStoreRepository, DomainEventRepository {

    private final SpringDataEventStoreRepository springRepo;
    private final EventStoreConverter converter;
    private final ObjectMapper objectMapper;

    public JpaEventStoreRepository(SpringDataEventStoreRepository springRepo,
                                   EventStoreConverter converter,
                                   ObjectMapper objectMapper) {
        this.springRepo = springRepo;
        this.converter = converter;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(EventStore eventStore) {
        EventStoreJpaEntity entity = converter.toJpaEntity(eventStore);
        springRepo.saveAndFlush(entity);
    }

    @Override
    public void append(DomainEvent event) {
        try {
            save(new EventStore(
                    UUID.randomUUID(),
                    event.getEventId(),
                    event.getEventType(),
                    event.getAggregateId(),
                    event.getAggregateType(),
                    event.getVersion(),
                    objectMapper.writeValueAsString(event)
            ));
        } catch (JsonProcessingException exception) {
            throw new EventSerializationException("Could not serialize domain event " + event.getEventId(), exception);
        }
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
