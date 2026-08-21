package com.pdonha.pix.adapter.out.persistence.converter;

import com.pdonha.pix.adapter.out.persistence.entity.EventStoreJpaEntity;
import com.pdonha.pix.domain.model.EventStore;
import org.springframework.stereotype.Component;

@Component
public class EventStoreConverter {
    
    public EventStore toDomain(EventStoreJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return EventStore.rehydrate(
            entity.getId(),
            entity.getEventId(),
            entity.getEventType(),
            entity.getAggregateId(),
            entity.getAggregateType(),
            entity.getAggregateVersion(),
            entity.getEventData(),
            entity.getStoredAt()
        );
    }
    
    public EventStoreJpaEntity toJpaEntity(EventStore domain) {
        if (domain == null) {
            return null;
        }
        
        EventStoreJpaEntity entity = new EventStoreJpaEntity();
        entity.setId(domain.getId());
        entity.setEventId(domain.getEventId());
        entity.setEventType(domain.getEventType());
        entity.setAggregateId(domain.getAggregateId());
        entity.setAggregateType(domain.getAggregateType());
        entity.setAggregateVersion(domain.getAggregateVersion());
        entity.setEventData(domain.getEventData());
        entity.setStoredAt(domain.getStoredAt());
        
        return entity;
    }
}
