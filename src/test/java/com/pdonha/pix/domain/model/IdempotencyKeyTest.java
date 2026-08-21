package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.exception.IdempotencyKeyInvalidException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class IdempotencyKeyTest {

    @Test
    void shouldCreateIdempotencyKeyWithValidData() {
        String key = "test-key-123";
        UUID transferId = UUID.randomUUID();
        IdempotencyStatus status = IdempotencyStatus.PENDING;

        IdempotencyKey idempotencyKey = new IdempotencyKey(key, transferId, status);

        assertNotNull(idempotencyKey.getId());
        assertEquals(key, idempotencyKey.getKey());
        assertEquals(transferId, idempotencyKey.getTransferId());
        assertEquals(status, idempotencyKey.getStatus());
        assertNotNull(idempotencyKey.getCreatedAt());
        assertNotNull(idempotencyKey.getUpdatedAt());
    }

    @Test
    void shouldThrowExceptionWhenKeyIsNull() {
        UUID transferId = UUID.randomUUID();
        IdempotencyStatus status = IdempotencyStatus.PENDING;

        assertThrows(IdempotencyKeyInvalidException.class, () ->
                new IdempotencyKey(null, transferId, status)
        );
    }

    @Test
    void shouldThrowExceptionWhenKeyIsBlank() {
        UUID transferId = UUID.randomUUID();
        IdempotencyStatus status = IdempotencyStatus.PENDING;

        assertThrows(IdempotencyKeyInvalidException.class, () ->
                new IdempotencyKey("   ", transferId, status)
        );
    }

    @Test
    void shouldThrowExceptionWhenTransferIdIsNull() {
        String key = "test-key-123";
        IdempotencyStatus status = IdempotencyStatus.PENDING;

        assertThrows(RuntimeException.class, () ->
                new IdempotencyKey(key, null, status)
        );
    }

    @Test
    void shouldThrowExceptionWhenStatusIsNull() {
        String key = "test-key-123";
        UUID transferId = UUID.randomUUID();

        assertThrows(RuntimeException.class, () ->
                new IdempotencyKey(key, transferId, null)
        );
    }

    @Test
    void shouldUpdateStatusSuccessfully() {
        String key = "test-key-456";
        UUID transferId = UUID.randomUUID();
        IdempotencyKey idempotencyKey = new IdempotencyKey(key, transferId, IdempotencyStatus.PENDING);

        idempotencyKey.setStatus(IdempotencyStatus.SUCCESS);

        assertEquals(IdempotencyStatus.SUCCESS, idempotencyKey.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenSettingStatusToNull() {
        String key = "test-key-789";
        UUID transferId = UUID.randomUUID();
        IdempotencyKey idempotencyKey = new IdempotencyKey(key, transferId, IdempotencyStatus.PENDING);

        assertThrows(RuntimeException.class, () ->
                idempotencyKey.setStatus(null)
        );
    }

    @Test
    void shouldConsiderEqualIfSameId() {
        String key = "test-key-equal";
        UUID transferId = UUID.randomUUID();
        IdempotencyKey key1 = new IdempotencyKey(key, transferId, IdempotencyStatus.PENDING);
        IdempotencyKey key2 = key1;

        assertEquals(key1, key2);
    }

    @Test
    void shouldNotConsiderEqualIfDifferentId() {
        String key = "test-key-diff";
        UUID transferId = UUID.randomUUID();
        IdempotencyKey key1 = new IdempotencyKey(key, transferId, IdempotencyStatus.PENDING);
        IdempotencyKey key2 = new IdempotencyKey(key, transferId, IdempotencyStatus.SUCCESS);

        assertNotEquals(key1, key2);
    }
}
