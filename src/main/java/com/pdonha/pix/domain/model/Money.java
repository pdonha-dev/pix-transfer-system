package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.exception.InvalidMoneyException;

import java.math.BigDecimal;

public final class Money {
    private final BigDecimal amount;

    public Money(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidMoneyException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidMoneyException("Amount cannot be negative");
        }

        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Money add(Money other) {
        return new Money(amount.add(other.amount));
    }

    public Money subtract(Money other) {
        return new Money(amount.subtract(other.amount));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Money money = (Money) obj;
        return amount.equals(money.amount);
    }

    @Override
    public int hashCode() {
        return amount.hashCode();
    }

    public boolean isGreaterThanOrEqual(Money other) {
        return this.amount.compareTo(other.amount) >= 0;
    }

    public boolean isLessThan(Money other){
        return this.amount.compareTo(other.amount) < 0;
    }

    public boolean isGreaterThan(Money other) {
        return this.amount.compareTo(other.amount) > 0;
    }
}