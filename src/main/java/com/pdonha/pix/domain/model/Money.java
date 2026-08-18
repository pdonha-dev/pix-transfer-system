package com.pdonha.pix.domain.model;

import java.math.BigDecimal;

public final class Money {
    private final BigDecimal amount;

    public Money(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor não pode ser negativo");
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
}