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
    private final List<DomainEvent> events;

    public Account(UUID id, UUID customerId, Money initialBalance, Money dailyLimit) {
        if(id == null) {
            throw new InvalidAccountException("Account ID cannot be null");
        }
        if(customerId == null) {
            throw new InvalidAccountException("Customer ID cannot be null");
        }
        if(initialBalance == null) {
            throw new InvalidAccountException("Initial balance cannot be null");
        }
        if(!initialBalance.isGreaterThanOrEqual(new Money(BigDecimal.ZERO))) {
            throw new InvalidAccountException("Initial balance cannot be negative");
        }
        if(dailyLimit == null) {
            throw new InvalidAccountException("Daily limit cannot be null");
        }
        if(dailyLimit.isLessThan(new Money(BigDecimal.ONE))) {
            throw new InvalidAccountException("Daily limit must be at least 1.00");
        }

        this.id = id;
        this.customerId = customerId;
        this.balance = initialBalance;
        this.dailyLimit = dailyLimit;
        this.dailyUsed = new Money(BigDecimal.ZERO);
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.events = new ArrayList<>();
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

    public List<DomainEvent> getEvents() {
        return new ArrayList<>(events);
    }

    public void addEvent(DomainEvent event) {
        this.events.add(event);
    }

    public void clearEvents() {
        this.events.clear();
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