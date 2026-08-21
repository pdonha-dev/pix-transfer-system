package com.pdonha.pix.application.service;

import com.pdonha.pix.adapter.out.persistence.repository.JpaAccountRepository;
import com.pdonha.pix.adapter.out.persistence.repository.JpaPixKeyRepository;
import com.pdonha.pix.adapter.out.persistence.repository.JpaTransferRepository;
import com.pdonha.pix.application.dto.command.CreatePixTransferCommand;
import com.pdonha.pix.domain.exception.AccountNotFoundException;
import com.pdonha.pix.domain.exception.PixKeyNotFoundException;
import com.pdonha.pix.domain.model.Account;
import com.pdonha.pix.domain.model.Money;
import com.pdonha.pix.domain.model.PixKey;
import com.pdonha.pix.domain.model.PixKeyType;
import com.pdonha.pix.domain.model.TransferStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CreatePixTransferServiceIT {

    @Autowired
    private CreatePixTransferService service;

    @Autowired
    private JpaAccountRepository accountRepository;

    @Autowired
    private JpaPixKeyRepository pixKeyRepository;

    @Autowired
    private JpaTransferRepository transferRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    @Rollback
    void shouldTransferPixBetweenAccounts() {
        // Arrange
        UUID accountAId = UUID.randomUUID();
        UUID accountBId = UUID.randomUUID();
        UUID customerAId = UUID.randomUUID();
        UUID customerBId = UUID.randomUUID();

        Account accountA = new Account(
                accountAId,
                customerAId,
                new Money(new BigDecimal("1000.00")),
                new Money(new BigDecimal("5000.00"))
        );

        Account accountB = new Account(
                accountBId,
                customerBId,
                new Money(BigDecimal.ZERO),
                new Money(new BigDecimal("5000.00"))
        );

        accountRepository.save(accountA);
        accountRepository.save(accountB);

        PixKey keyA = new PixKey(UUID.randomUUID(), accountAId, PixKeyType.CPF, "12345678900");
        PixKey keyB = new PixKey(UUID.randomUUID(), accountBId, PixKeyType.EMAIL, "user@example.com");

        pixKeyRepository.save(keyA);
        pixKeyRepository.save(keyB);

        // Act
        CreatePixTransferCommand cmd = new CreatePixTransferCommand(
                "12345678900",
                "user@example.com",
                new BigDecimal("100.00")
        );

        var result = service.execute(cmd);

        entityManager.clear();

        // Assert
        assertNotNull(result.getTransferId());
        assertEquals(TransferStatus.PENDING, result.getStatus());
        assertEquals(0, new BigDecimal("100.00").compareTo(result.getAmount().getAmount()));

        Account accountAAfter = accountRepository.findById(accountAId);
        Account accountBAfter = accountRepository.findById(accountBId);

        assertEquals(0, new BigDecimal("900.00").compareTo(accountAAfter.getBalance().getAmount()));
        assertEquals(0, new BigDecimal("100.00").compareTo(accountBAfter.getBalance().getAmount()));
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowWhenOriginPixKeyNotFound() {
        // Arrange
        UUID accountBId = UUID.randomUUID();
        Account accountB = new Account(
                accountBId,
                UUID.randomUUID(),
                new Money(BigDecimal.ZERO),
                new Money(new BigDecimal("5000.00"))
        );
        accountRepository.save(accountB);

        PixKey keyB = new PixKey(UUID.randomUUID(), accountBId, PixKeyType.EMAIL, "dest001@example.com");
        pixKeyRepository.save(keyB);

        // Act & Assert
        CreatePixTransferCommand cmd = new CreatePixTransferCommand(
                "nonexistent.001@example.com",
                "dest001@example.com",
                new BigDecimal("100.00")
        );

        assertThrows(PixKeyNotFoundException.class, () -> service.execute(cmd));
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowWhenDestinationPixKeyNotFound() {
        // Arrange
        UUID accountAId = UUID.randomUUID();
        Account accountA = new Account(
                accountAId,
                UUID.randomUUID(),
                new Money(new BigDecimal("1000.00")),
                new Money(new BigDecimal("5000.00"))
        );
        accountRepository.save(accountA);

        PixKey keyA = new PixKey(UUID.randomUUID(), accountAId, PixKeyType.CPF, "12345678902");
        pixKeyRepository.save(keyA);

        // Act & Assert
        CreatePixTransferCommand cmd = new CreatePixTransferCommand(
                "12345678902",
                "nonexistent.002@example.com",
                new BigDecimal("100.00")
        );

        assertThrows(PixKeyNotFoundException.class, () -> service.execute(cmd));
    }

    @Test
    @Transactional
    @Rollback
    void shouldPersistTransferToDatabase() {
        // Arrange
        UUID accountAId = UUID.randomUUID();
        UUID accountBId = UUID.randomUUID();

        Account accountA = new Account(
                accountAId,
                UUID.randomUUID(),
                new Money(new BigDecimal("500.00")),
                new Money(new BigDecimal("5000.00"))
        );
        Account accountB = new Account(
                accountBId,
                UUID.randomUUID(),
                new Money(BigDecimal.ZERO),
                new Money(new BigDecimal("5000.00"))
        );

        accountRepository.save(accountA);
        accountRepository.save(accountB);

        PixKey keyA = new PixKey(UUID.randomUUID(), accountAId, PixKeyType.CPF, "11122233344");
        PixKey keyB = new PixKey(UUID.randomUUID(), accountBId, PixKeyType.PHONE, "+5521987654321");

        pixKeyRepository.save(keyA);
        pixKeyRepository.save(keyB);

        // Act
        CreatePixTransferCommand cmd = new CreatePixTransferCommand(
                "11122233344",
                "+5521987654321",
                new BigDecimal("250.50")
        );

        var result = service.execute(cmd);

        entityManager.flush();
        entityManager.clear();

        // Assert - verify transfer persisted
        var transferLoaded = transferRepository.findById(result.getTransferId());
        assertNotNull(transferLoaded);
        assertEquals(TransferStatus.PENDING, transferLoaded.getStatus());
        assertEquals(0, new BigDecimal("250.50").compareTo(transferLoaded.getAmount().getAmount()));
    }
}
