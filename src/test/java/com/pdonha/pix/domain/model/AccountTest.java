package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.exception.AccountAlreadyActiveException;
import com.pdonha.pix.domain.exception.AccountBlockedException;
import com.pdonha.pix.domain.exception.DailyLimitExceededException;
import com.pdonha.pix.domain.exception.InsufficientBalanceException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountTest {

    private Account createAccount(BigDecimal balance, BigDecimal dailyLimit) {
        return new Account(UUID.randomUUID(), UUID.randomUUID(), new Money(balance), new Money(dailyLimit));
    }

    @Test
    void withdraw_shouldDecreaseBalance_whenBalanceIsGreaterThanAmount() {
        // Arrange
        Account account = createAccount(new BigDecimal("100.00"), new BigDecimal("1000.00"));
        Money amount = new Money(new BigDecimal("40.00"));

        // Act
        account.withdraw(amount);

        // Assert
        assertEquals(new Money(new BigDecimal("60.00")), account.getBalance());
        assertEquals(new Money(new BigDecimal("40.00")), account.getDailyUsed());
    }

    @Test
    void withdraw_shouldThrowInsufficientBalanceException_whenBalanceIsLessThanAmount() {
        // Arrange
        Account account = createAccount(new BigDecimal("50.00"), new BigDecimal("1000.00"));
        Money amount = new Money(new BigDecimal("100.00"));

        // Act & Assert
        assertThrows(InsufficientBalanceException.class, () -> account.withdraw(amount));
    }

    @Test
    void withdraw_shouldThrowDailyLimitExceededException_whenDailyUsedPlusAmountExceedsLimit() {
        // Arrange
        Account account = createAccount(new BigDecimal("1000.00"), new BigDecimal("100.00"));
        account.withdraw(new Money(new BigDecimal("80.00")));
        Money amount = new Money(new BigDecimal("30.00"));

        // Act & Assert
        assertThrows(DailyLimitExceededException.class, () -> account.withdraw(amount));
    }

    @Test
    void block_shouldDeactivateAccount_whenAccountIsActive() {
        // Arrange
        Account account = createAccount(new BigDecimal("100.00"), new BigDecimal("1000.00"));

        // Act
        account.block();

        // Assert
        assertFalse(account.isActive());
    }

    @Test
    void block_shouldThrowAccountBlockedException_whenAccountIsAlreadyBlocked() {
        // Arrange
        Account account = createAccount(new BigDecimal("100.00"), new BigDecimal("1000.00"));
        account.block();

        // Act & Assert
        assertThrows(AccountBlockedException.class, account::block);
    }

    @Test
    void unblock_shouldActivateAccount_whenAccountIsBlocked() {
        // Arrange
        Account account = createAccount(new BigDecimal("100.00"), new BigDecimal("1000.00"));
        account.block();

        // Act
        account.unblock();

        // Assert
        assertTrue(account.isActive());
    }

    @Test
    void unblock_shouldThrowAccountAlreadyActiveException_whenAccountIsAlreadyActive() {
        // Arrange
        Account account = createAccount(new BigDecimal("100.00"), new BigDecimal("1000.00"));

        // Act & Assert
        assertThrows(AccountAlreadyActiveException.class, account::unblock);
    }

    @Test
    void withdraw_shouldThrowAccountBlockedException_whenAccountIsBlocked() {
        // Arrange
        Account account = createAccount(new BigDecimal("100.00"), new BigDecimal("1000.00"));
        account.block();
        Money amount = new Money(new BigDecimal("10.00"));

        // Act & Assert
        assertThrows(AccountBlockedException.class, () -> account.withdraw(amount));
    }
}
