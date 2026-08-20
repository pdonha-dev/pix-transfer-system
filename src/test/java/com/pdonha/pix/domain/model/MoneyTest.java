package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.exception.PixException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyTest {

    @Test
    void add_shouldReturnSumOfValues_whenAddingHundredAndFifty() {
        // Arrange
        Money money = new Money(new BigDecimal("100"));
        Money other = new Money(new BigDecimal("50"));

        // Act
        Money result = money.add(other);

        // Assert
        assertEquals(new Money(new BigDecimal("150")), result);
    }

    @Test
    void subtract_shouldReturnDifferenceOfValues_whenSubtractingThirtyFromHundred() {
        // Arrange
        Money money = new Money(new BigDecimal("100"));
        Money other = new Money(new BigDecimal("30"));

        // Act
        Money result = money.subtract(other);

        // Assert
        assertEquals(new Money(new BigDecimal("70")), result);
    }

    @Test
    void subtract_shouldThrowPixException_whenResultIsNegative() {
        // Arrange
        Money money = new Money(new BigDecimal("50"));
        Money other = new Money(new BigDecimal("100"));

        // Act & Assert
        assertThrows(PixException.class, () -> money.subtract(other));
    }

    @Test
    void constructor_shouldMaintainScaleOfTwo() {
        // Arrange & Act
        Money money = new Money(new BigDecimal("10.10"));

        // Assert
        assertEquals(2, money.getAmount().scale());
    }
}
