package com.pdonha.pix.domain.port;

import com.pdonha.pix.domain.model.EventStore;
import java.util.List;
import java.util.UUID;

public interface EventStoreRepository {
    void save(EventStore eventStore);
    List<EventStore> findByAggregateId(UUID aggregateId);
    List<EventStore> findByAggregateIdAndType(UUID aggregateId, String aggregateType);
}
