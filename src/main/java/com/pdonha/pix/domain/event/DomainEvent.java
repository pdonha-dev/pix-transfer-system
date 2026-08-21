package com.pdonha.pix.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class DomainEvent {
    private final UUID eventId;
    private final UUID aggregateId;
    private final String aggregateType;
    private final LocalDateTime occurredAt;
    private final int version;

    protected DomainEvent(UUID aggregateId, String aggregateType, int version) {
        this.eventId = UUID.randomUUID();
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.version = version;
        this.occurredAt = LocalDateTime.now();
    }

    public UUID getEventId() {
        return eventId;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public int getVersion() {
        return version;
    }

    public abstract String getEventType();
}
