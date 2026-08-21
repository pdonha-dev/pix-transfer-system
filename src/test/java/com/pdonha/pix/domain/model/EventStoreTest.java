package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.exception.InvalidEventStoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("EventStore")
class EventStoreTest {

    @Test
    @DisplayName("should create event store with valid data")
    void shouldCreateWithValidData() {
        UUID id = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        UUID aggregateId = UUID.randomUUID();
        String eventData = """
            {"accountId": "123", "balance": "1000.00"}
            """;

        EventStore eventStore = new EventStore(id, eventId, "AccountCreated", aggregateId, "Account", 1, eventData);

        assertEquals(id, eventStore.getId());
        assertEquals(eventId, eventStore.getEventId());
        assertEquals("AccountCreated", eventStore.getEventType());
        assertEquals(aggregateId, eventStore.getAggregateId());
        assertEquals("Account", eventStore.getAggregateType());
        assertEquals(1, eventStore.getAggregateVersion());
        assertEquals(eventData, eventStore.getEventData());
    }

    @Test
    @DisplayName("should throw when ID is null")
    void shouldThrowWhenIdNull() {
        assertThrows(InvalidEventStoreException.class, () -> {
            new EventStore(null, UUID.randomUUID(), "AccountCreated", UUID.randomUUID(), "Account", 1, "{}");
        });
    }

    @Test
    @DisplayName("should throw when event type is blank")
    void shouldThrowWhenEventTypeBlank() {
        assertThrows(InvalidEventStoreException.class, () -> {
            new EventStore(UUID.randomUUID(), UUID.randomUUID(), "", UUID.randomUUID(), "Account", 1, "{}");
        });
    }

    @Test
    @DisplayName("should throw when event data is blank")
    void shouldThrowWhenEventDataBlank() {
        assertThrows(InvalidEventStoreException.class, () -> {
            new EventStore(UUID.randomUUID(), UUID.randomUUID(), "AccountCreated", UUID.randomUUID(), "Account", 1, "");
        });
    }
}
