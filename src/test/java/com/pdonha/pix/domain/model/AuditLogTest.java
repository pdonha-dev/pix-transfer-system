package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.exception.InvalidAuditLogException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("AuditLog")
class AuditLogTest {

    @Test
    @DisplayName("should create audit log with valid data")
    void shouldCreateWithValidData() {
        UUID id = UUID.randomUUID();
        UUID entityId = UUID.randomUUID();
        LocalDateTime changedAt = LocalDateTime.now();

        AuditLog auditLog = new AuditLog(id, "Account", entityId, "CREATE", "user@example.com", null, "{\"balance\": \"1000\"}", changedAt);

        assertEquals(id, auditLog.getId());
        assertEquals("Account", auditLog.getEntityType());
        assertEquals(entityId, auditLog.getEntityId());
        assertEquals("CREATE", auditLog.getAction());
        assertEquals("user@example.com", auditLog.getChangedBy());
        assertEquals("{\"balance\": \"1000\"}", auditLog.getNewValue());
    }

    @Test
    @DisplayName("should throw when entity type is blank")
    void shouldThrowWhenEntityTypeBlank() {
        assertThrows(InvalidAuditLogException.class, () -> {
            new AuditLog(UUID.randomUUID(), "", UUID.randomUUID(), "CREATE", "user@example.com", null, "{}", LocalDateTime.now());
        });
    }

    @Test
    @DisplayName("should throw when changed by is blank")
    void shouldThrowWhenChangedByBlank() {
        assertThrows(InvalidAuditLogException.class, () -> {
            new AuditLog(UUID.randomUUID(), "Account", UUID.randomUUID(), "CREATE", "", null, "{}", LocalDateTime.now());
        });
    }
}
