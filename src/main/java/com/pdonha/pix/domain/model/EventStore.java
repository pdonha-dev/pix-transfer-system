package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.exception.InvalidEventStoreException;

import java.time.LocalDateTime;
import java.util.UUID;

public class EventStore {
    private final UUID id;
    private final UUID eventId;
    private final String eventType;
    private final UUID aggregateId;
    private final String aggregateType;
    private final int aggregateVersion;
    private final String eventData;
    private final LocalDateTime storedAt;

    public EventStore(UUID id, UUID eventId, String eventType, UUID aggregateId, 
                     String aggregateType, int aggregateVersion, String eventData) {
        if (id == null) {
            throw new InvalidEventStoreException("Event store ID cannot be null");
        }
        if (eventId == null) {
            throw new InvalidEventStoreException("Event ID cannot be null");
        }
        if (eventType == null || eventType.isBlank()) {
            throw new InvalidEventStoreException("Event type cannot be null or blank");
        }
        if (aggregateId == null) {
            throw new InvalidEventStoreException("Aggregate ID cannot be null");
        }
        if (aggregateType == null || aggregateType.isBlank()) {
            throw new InvalidEventStoreException("Aggregate type cannot be null or blank");
        }
        if (eventData == null || eventData.isBlank()) {
            throw new InvalidEventStoreException("Event data cannot be null or blank");
        }

        this.id = id;
        this.eventId = eventId;
        this.eventType = eventType;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.aggregateVersion = aggregateVersion;
        this.eventData = eventData;
        this.storedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public int getAggregateVersion() {
        return aggregateVersion;
    }

    public String getEventData() {
        return eventData;
    }

    public LocalDateTime getStoredAt() {
        return storedAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EventStore eventStore = (EventStore) obj;
        return id.equals(eventStore.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
