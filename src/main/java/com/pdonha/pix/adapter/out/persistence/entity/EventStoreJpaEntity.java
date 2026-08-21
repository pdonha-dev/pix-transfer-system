package com.pdonha.pix.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "event_store")
public class EventStoreJpaEntity {
    
    @Id
    private UUID id;
    
    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;
    
    @Column(name = "event_type", nullable = false)
    private String eventType;
    
    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;
    
    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;
    
    @Column(name = "aggregate_version", nullable = false)
    private int aggregateVersion;
    
    @Column(name = "event_data", nullable = false, columnDefinition = "TEXT")
    private String eventData;
    
    @Column(name = "stored_at", nullable = false, updatable = false)
    private LocalDateTime storedAt;
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getEventId() {
        return eventId;
    }
    
    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public UUID getAggregateId() {
        return aggregateId;
    }
    
    public void setAggregateId(UUID aggregateId) {
        this.aggregateId = aggregateId;
    }
    
    public String getAggregateType() {
        return aggregateType;
    }
    
    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }
    
    public int getAggregateVersion() {
        return aggregateVersion;
    }
    
    public void setAggregateVersion(int aggregateVersion) {
        this.aggregateVersion = aggregateVersion;
    }
    
    public String getEventData() {
        return eventData;
    }
    
    public void setEventData(String eventData) {
        this.eventData = eventData;
    }
    
    public LocalDateTime getStoredAt() {
        return storedAt;
    }
    
    public void setStoredAt(LocalDateTime storedAt) {
        this.storedAt = storedAt;
    }
}
