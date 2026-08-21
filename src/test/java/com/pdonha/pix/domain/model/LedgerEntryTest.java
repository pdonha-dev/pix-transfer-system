package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.exception.InvalidLedgerEntryException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LedgerEntryTest {

    @Test
    void shouldCreateDebitWithResultingBalance() {
        Account account = new Account(
                UUID.randomUUID(), UUID.randomUUID(),
                new Money(new BigDecimal("1000.00")), new Money(new BigDecimal("5000.00")));
        Money amount = new Money(new BigDecimal("100.00"));
        account.withdraw(amount);

        LedgerEntry entry = LedgerEntry.debit(UUID.randomUUID(), account, amount);

        assertEquals(LedgerEntryType.DEBIT, entry.getType());
        assertEquals(new BigDecimal("900.00"), entry.getBalanceAfter().getAmount());
    }

    @Test
    void shouldRejectZeroAmount() {
        assertThrows(InvalidLedgerEntryException.class, () -> new LedgerEntry(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LedgerEntryType.CREDIT,
                new Money(BigDecimal.ZERO), new Money(BigDecimal.ZERO), LocalDateTime.now()));
    }
}
