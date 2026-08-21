package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.exception.InvalidAuditLogException;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuditLog {
    private final UUID id;
    private final String entityType;
    private final UUID entityId;
    private final String action;
    private final String changedBy;
    private final String oldValue;
    private final String newValue;
    private final LocalDateTime changedAt;

    public AuditLog(UUID id, String entityType, UUID entityId, String action, 
                   String changedBy, String oldValue, String newValue, LocalDateTime changedAt) {
        if (id == null) {
            throw new InvalidAuditLogException("Audit log ID cannot be null");
        }
        if (entityType == null || entityType.isBlank()) {
            throw new InvalidAuditLogException("Entity type cannot be null or blank");
        }
        if (entityId == null) {
            throw new InvalidAuditLogException("Entity ID cannot be null");
        }
        if (action == null || action.isBlank()) {
            throw new InvalidAuditLogException("Action cannot be null or blank");
        }
        if (changedBy == null || changedBy.isBlank()) {
            throw new InvalidAuditLogException("Changed by cannot be null or blank");
        }
        if (changedAt == null) {
            throw new InvalidAuditLogException("Changed at cannot be null");
        }

        this.id = id;
        this.entityType = entityType;
        this.entityId = entityId;
        this.action = action;
        this.changedBy = changedBy;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedAt = changedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getEntityType() {
        return entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public String getAction() {
        return action;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AuditLog auditLog = (AuditLog) obj;
        return id.equals(auditLog.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
