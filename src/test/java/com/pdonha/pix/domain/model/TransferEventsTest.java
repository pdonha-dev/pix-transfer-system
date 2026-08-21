package com.pdonha.pix.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TransferEventsTest")
class TransferEventsTest {

    @Test
    @DisplayName("should have empty events on creation")
    void shouldHaveEmptyEventsOnCreation() {
        UUID transferId = UUID.randomUUID();
        UUID payerAccountId = UUID.randomUUID();
        UUID payeeAccountId = UUID.randomUUID();
        Money amount = new Money(BigDecimal.valueOf(100));

        Transfer transfer = new Transfer(transferId, payerAccountId, payeeAccountId, amount);

        assertTrue(transfer.getEvents().isEmpty());
    }

    @Test
    @DisplayName("should collect events via addEvent")
    void shouldCollectEventsViaAddEvent() {
        UUID transferId = UUID.randomUUID();
        UUID payerAccountId = UUID.randomUUID();
        UUID payeeAccountId = UUID.randomUUID();
        Money amount = new Money(BigDecimal.valueOf(100));

        Transfer transfer = new Transfer(transferId, payerAccountId, payeeAccountId, amount);
        com.pdonha.pix.domain.event.TransferCreatedEvent event = 
            new com.pdonha.pix.domain.event.TransferCreatedEvent(transferId, payerAccountId, payeeAccountId, amount, "user@example.com");

        transfer.addEvent(event);

        assertEquals(1, transfer.getEvents().size());
        assertEquals("TransferCreated", transfer.getEvents().get(0).getEventType());
    }

    @Test
    @DisplayName("should clear events after publishing")
    void shouldClearEventsAfterPublishing() {
        UUID transferId = UUID.randomUUID();
        UUID payerAccountId = UUID.randomUUID();
        UUID payeeAccountId = UUID.randomUUID();
        Money amount = new Money(BigDecimal.valueOf(100));

        Transfer transfer = new Transfer(transferId, payerAccountId, payeeAccountId, amount);
        com.pdonha.pix.domain.event.TransferCreatedEvent event = 
            new com.pdonha.pix.domain.event.TransferCreatedEvent(transferId, payerAccountId, payeeAccountId, amount, "user@example.com");

        transfer.addEvent(event);
        assertEquals(1, transfer.getEvents().size());

        transfer.clearEvents();
        assertEquals(0, transfer.getEvents().size());
    }
}
