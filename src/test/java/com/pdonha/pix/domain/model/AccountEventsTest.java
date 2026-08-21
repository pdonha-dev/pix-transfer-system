package com.pdonha.pix.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AccountEventsTest")
class AccountEventsTest {

    @Test
    @DisplayName("should publish AccountCreated event on initialization")
    void shouldHaveEventListOnCreation() {
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Money initialBalance = new Money(BigDecimal.valueOf(1000));
        Money dailyLimit = new Money(BigDecimal.valueOf(5000));

        Account account = new Account(accountId, customerId, initialBalance, dailyLimit);

        assertTrue(account.getEvents().isEmpty());
    }

    @Test
    @DisplayName("should collect events via addEvent")
    void shouldCollectEventsViaAddEvent() {
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Money initialBalance = new Money(BigDecimal.valueOf(1000));
        Money dailyLimit = new Money(BigDecimal.valueOf(5000));

        Account account = new Account(accountId, customerId, initialBalance, dailyLimit);
        com.pdonha.pix.domain.event.AccountCreatedEvent event = 
            new com.pdonha.pix.domain.event.AccountCreatedEvent(accountId, customerId, initialBalance, dailyLimit, "user@example.com");

        account.addEvent(event);

        assertEquals(1, account.getEvents().size());
        assertEquals("AccountCreated", account.getEvents().get(0).getEventType());
    }

    @Test
    @DisplayName("should clear events after publishing")
    void shouldClearEventsAfterPublishing() {
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Money initialBalance = new Money(BigDecimal.valueOf(1000));
        Money dailyLimit = new Money(BigDecimal.valueOf(5000));

        Account account = new Account(accountId, customerId, initialBalance, dailyLimit);
        com.pdonha.pix.domain.event.AccountCreatedEvent event = 
            new com.pdonha.pix.domain.event.AccountCreatedEvent(accountId, customerId, initialBalance, dailyLimit, "user@example.com");

        account.addEvent(event);
        assertEquals(1, account.getEvents().size());

        account.clearEvents();
        assertEquals(0, account.getEvents().size());
    }
}
