package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.exception.IdempotencyKeyInvalidException;
import com.pdonha.pix.domain.exception.InvalidIdempotencyKeyException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IdempotencyKeyTest {

    @Test
    void shouldCreatePendingIdempotencyKeyWithValidData() {
        UUID transferId = UUID.randomUUID();

        IdempotencyKey key = new IdempotencyKey("test-key-123", transferId, "request-hash");

        assertNotNull(key.getId());
        assertEquals(transferId, key.getTransferId());
        assertEquals("request-hash", key.getRequestHash());
        assertEquals(IdempotencyStatus.PENDING, key.getStatus());
    }

    @Test
    void shouldRejectInvalidCreationData() {
        UUID transferId = UUID.randomUUID();

        assertThrows(IdempotencyKeyInvalidException.class,
                () -> new IdempotencyKey(null, transferId, "hash"));
        assertThrows(IdempotencyKeyInvalidException.class,
                () -> new IdempotencyKey(" ", transferId, "hash"));
        assertThrows(InvalidIdempotencyKeyException.class,
                () -> new IdempotencyKey("key", null, "hash"));
        assertThrows(InvalidIdempotencyKeyException.class,
                () -> new IdempotencyKey("key", transferId, " "));
    }

    @Test
    void shouldTransitionOnlyFromPending() {
        IdempotencyKey key = new IdempotencyKey("key", UUID.randomUUID(), "hash");

        key.markSuccessful();

        assertEquals(IdempotencyStatus.SUCCESS, key.getStatus());
        assertThrows(InvalidIdempotencyKeyException.class, key::markFailed);
    }

    @Test
    void shouldRehydratePersistedStateWithoutLosingIdentity() {
        UUID id = UUID.randomUUID();
        UUID transferId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusMinutes(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        IdempotencyKey key = IdempotencyKey.rehydrate(
                id, "key", transferId, "hash", IdempotencyStatus.FAILED, createdAt, updatedAt);

        assertEquals(id, key.getId());
        assertEquals(createdAt, key.getCreatedAt());
        assertEquals(updatedAt, key.getUpdatedAt());
        assertEquals(IdempotencyStatus.FAILED, key.getStatus());
    }
}
