package com.pdonha.pix.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    public Account(UUID id, UUID customerId, Money initialBalance, Money dailyLimit) {
        if(id == null) {
            throw new IllegalArgumentException("Id não pode ser nulo");
        }
        if(customerId == null) {
            throw new IllegalArgumentException("Id do cliente não pode ser nulo");
        }
        if(initialBalance == null) {
            throw new IllegalArgumentException("Saldo inicial não pode ser nulo");
        }
        if(!initialBalance.isGreaterThanOrEqual(new Money(BigDecimal.ZERO))) {
            throw new IllegalArgumentException("Saldo inicial não pode ser negativo");
        }
        if(dailyLimit == null) {
            throw new IllegalArgumentException("Limite diário não pode ser nulo");
        }
        if(dailyLimit.isLessThan(new Money(BigDecimal.ONE))) {
            throw new IllegalArgumentException("Limite diário deve ser maior que zero");
        }

        this.id = id;
        this.customerId = customerId;
        this.balance = initialBalance;
        this.dailyLimit = dailyLimit;
        this.dailyUsed = new Money(BigDecimal.ZERO);
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    public void deposit(Money amount) {
        if (!isActive) {
            throw new IllegalArgumentException("Conta bloqueada");
        }
        this.balance = balance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void withdraw(Money amount) {
        if (!isActive) {
            throw new IllegalArgumentException("Conta bloqueada");
        }
        if (balance.isLessThan(amount)) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }
        if (dailyLimit.isLessThan(dailyUsed.add(amount))) {
            throw new IllegalArgumentException("Limite diário excedido");
        }
        this.balance = balance.subtract(amount);
        this.dailyUsed = dailyUsed.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void block() {
        if (!isActive) {
            throw new IllegalArgumentException("Conta já está bloqueada");
        }
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void unblock() {
        if (isActive) {
            throw new IllegalArgumentException("Conta já está ativa");
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