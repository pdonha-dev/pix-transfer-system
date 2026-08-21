package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.event.AccountCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Account pending events")
class AccountEventsTest {

    @Test
    void shouldStartWithoutPendingEvents() {
        assertTrue(account().getPendingEvents().isEmpty());
    }

    @Test
    void shouldCollectAndDrainPendingEvents() {
        Account account = account();
        account.addPendingEvent(new AccountCreatedEvent(
                account.getId(), account.getCustomerId(), account.getBalance(),
                account.getDailyLimit(), "operator"));

        assertEquals(1, account.getPendingEvents().size());
        assertEquals(1, account.drainPendingEvents().size());
        assertTrue(account.getPendingEvents().isEmpty());
    }

    private Account account() {
        return new Account(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new Money(new BigDecimal("1000.00")),
                new Money(new BigDecimal("5000.00"))
        );
    }
}
