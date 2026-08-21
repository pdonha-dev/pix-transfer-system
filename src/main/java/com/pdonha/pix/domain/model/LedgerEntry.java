package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.exception.InvalidLedgerEntryException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public final class LedgerEntry {
    private final UUID id;
    private final UUID transferId;
    private final UUID accountId;
    private final LedgerEntryType type;
    private final Money amount;
    private final Money balanceAfter;
    private final LocalDateTime createdAt;

    public LedgerEntry(UUID id, UUID transferId, UUID accountId, LedgerEntryType type,
                       Money amount, Money balanceAfter, LocalDateTime createdAt) {
        if (id == null || transferId == null || accountId == null) {
            throw new InvalidLedgerEntryException("Ledger entry identifiers cannot be null");
        }
        if (type == null || amount == null || balanceAfter == null || createdAt == null) {
            throw new InvalidLedgerEntryException("Ledger entry state cannot contain null fields");
        }
        if (!amount.isGreaterThan(new Money(BigDecimal.ZERO))) {
            throw new InvalidLedgerEntryException("Ledger entry amount must be positive");
        }
        this.id = id;
        this.transferId = transferId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.createdAt = createdAt;
    }

    public static LedgerEntry debit(UUID transferId, Account account, Money amount) {
        return new LedgerEntry(UUID.randomUUID(), transferId, account.getId(), LedgerEntryType.DEBIT,
                amount, account.getBalance(), LocalDateTime.now());
    }

    public static LedgerEntry credit(UUID transferId, Account account, Money amount) {
        return new LedgerEntry(UUID.randomUUID(), transferId, account.getId(), LedgerEntryType.CREDIT,
                amount, account.getBalance(), LocalDateTime.now());
    }

    public UUID getId() {
        return id;
    }

    public UUID getTransferId() {
        return transferId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public LedgerEntryType getType() {
        return type;
    }

    public Money getAmount() {
        return amount;
    }

    public Money getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
