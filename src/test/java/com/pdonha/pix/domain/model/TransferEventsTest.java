package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.event.TransferCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Transfer pending events")
class TransferEventsTest {

    @Test
    void shouldEmitCreatedEventForNewTransfer() {
        Transfer transfer = transfer();

        assertEquals(1, transfer.getPendingEvents().size());
        assertEquals("TransferCreated", transfer.getPendingEvents().getFirst().getEventType());
    }

    @Test
    void shouldDrainPendingEventsOnce() {
        Transfer transfer = transfer();

        List<com.pdonha.pix.domain.event.DomainEvent> drained = transfer.drainPendingEvents();

        assertEquals(1, drained.size());
        assertTrue(transfer.getPendingEvents().isEmpty());
        assertTrue(transfer.drainPendingEvents().isEmpty());
    }

    @Test
    void shouldAcceptAdditionalPendingEvent() {
        Transfer transfer = transfer();
        transfer.addPendingEvent(new TransferCreatedEvent(
                transfer.getId(), transfer.getPayerAccountId(), transfer.getPayeeAccountId(),
                transfer.getAmount(), "operator"));

        assertEquals(2, transfer.getPendingEvents().size());
    }

    private Transfer transfer() {
        return new Transfer(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                new Money(new BigDecimal("100.00"))
        );
    }
}
