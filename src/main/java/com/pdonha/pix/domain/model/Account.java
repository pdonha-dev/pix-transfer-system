package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.exception.AccountAlreadyActiveException;
import com.pdonha.pix.domain.exception.AccountBlockedException;
import com.pdonha.pix.domain.exception.DailyLimitExceededException;
import com.pdonha.pix.domain.exception.InsufficientBalanceException;
import com.pdonha.pix.domain.exception.InvalidAccountException;
import com.pdonha.pix.domain.event.DomainEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Account {
    private final UUID id;
    private final UUID customerId;
    private Money balance;
    private final Money dailyLimit;
    private Money dailyUsed;
    private boolean isActive;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<DomainEvent> pendingEvents;
    private final Long version;

    public Account(UUID id, UUID customerId, Money initialBalance, Money dailyLimit) {
        this(id, customerId, initialBalance, dailyLimit, new Money(BigDecimal.ZERO), true,
                LocalDateTime.now(), LocalDateTime.now(), null);
    }

    private Account(UUID id, UUID customerId, Money balance, Money dailyLimit, Money dailyUsed,
                    boolean active, LocalDateTime createdAt, LocalDateTime updatedAt, Long version) {
        if(id == null) {
            throw new InvalidAccountException("Account ID cannot be null");
        }
        if(customerId == null) {
            throw new InvalidAccountException("Customer ID cannot be null");
        }
        if(balance == null) {
            throw new InvalidAccountException("Initial balance cannot be null");
        }
        if(!balance.isGreaterThanOrEqual(new Money(BigDecimal.ZERO))) {
            throw new InvalidAccountException("Initial balance cannot be negative");
        }
        if(dailyLimit == null) {
            throw new InvalidAccountException("Daily limit cannot be null");
        }
        if(dailyLimit.isLessThan(new Money(BigDecimal.ONE))) {
            throw new InvalidAccountException("Daily limit must be at least 1.00");
        }

        if (dailyUsed == null || createdAt == null || updatedAt == null) {
            throw new InvalidAccountException("Persisted account state cannot contain null fields");
        }
        this.id = id;
        this.customerId = customerId;
        this.balance = balance;
        this.dailyLimit = dailyLimit;
        this.dailyUsed = dailyUsed;
        this.isActive = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.pendingEvents = new ArrayList<>();
        this.version = version;
    }

    public static Account rehydrate(UUID id, UUID customerId, Money balance, Money dailyLimit,
                                    Money dailyUsed, boolean active, LocalDateTime createdAt,
                                    LocalDateTime updatedAt, Long version) {
        return new Account(id, customerId, balance, dailyLimit, dailyUsed, active, createdAt, updatedAt, version);
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public Money getBalance() {
        return balance;
    }

    public Money getDailyLimit() {
        return dailyLimit;
    }

    public Money getDailyUsed() {
        return dailyUsed;
    }

    public boolean isActive() {
        return isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public List<DomainEvent> getPendingEvents() {
        return List.copyOf(pendingEvents);
    }

    public void addPendingEvent(DomainEvent event) {
        if (event == null) {
            throw new InvalidAccountException("Domain event cannot be null");
        }
        pendingEvents.add(event);
    }

    public List<DomainEvent> drainPendingEvents() {
        List<DomainEvent> drained = List.copyOf(pendingEvents);
        pendingEvents.clear();
        return drained;
    }

    public void deposit(Money amount) {
        if (!isActive) {
            throw new AccountBlockedException("Account is blocked", id.toString());
        }
        this.balance = balance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void withdraw(Money amount) {
        if (!isActive) {
            throw new AccountBlockedException("Account is blocked", id.toString());
        }
        if (balance.isLessThan(amount)) {
            throw new InsufficientBalanceException("Insufficient balance for withdrawal");
        }
        if (dailyLimit.isLessThan(dailyUsed.add(amount))) {
            throw new DailyLimitExceededException("Daily transfer limit exceeded");
        }
        this.balance = balance.subtract(amount);
        this.dailyUsed = dailyUsed.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void block() {
        if (!isActive) {
            throw new AccountBlockedException("Account is already blocked", id.toString());
        }
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void unblock() {
        if (isActive) {
            throw new AccountAlreadyActiveException("Account is already active", id.toString());
        }
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Account account = (Account) obj;
        return id.equals(account.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}