package com.pdonha.pix.adapter.out.persistence.repository;

import com.pdonha.pix.domain.model.Account;
import com.pdonha.pix.domain.model.Money;
import com.pdonha.pix.domain.model.Transfer;
import com.pdonha.pix.domain.model.TransferStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class TransferRepositoryIT {

    @Autowired
    private JpaTransferRepository transferRepository;
    @Autowired
    private JpaAccountRepository accountRepository;
    @Autowired
    private JdbcTemplate jdbc;

    @AfterEach
    void cleanUp() {
        jdbc.update("DELETE FROM event_store");
        jdbc.update("DELETE FROM idempotency_keys");
        jdbc.update("DELETE FROM ledger_entries");
        jdbc.update("DELETE FROM transfers");
        jdbc.update("DELETE FROM pix_keys");
        jdbc.update("DELETE FROM accounts");
    }

    @Test
    void shouldPersistStatusAndIncrementVersionOnUpdate() {
        Account payer = account();
        Account payee = account();
        accountRepository.save(payer);
        accountRepository.save(payee);
        Transfer transfer = new Transfer(
                UUID.randomUUID(), payer.getId(), payee.getId(), new Money(new BigDecimal("25.00")));

        transferRepository.save(transfer);
        Transfer persisted = transferRepository.findById(transfer.getId());
        Long initialVersion = persisted.getVersion();
        persisted.complete();
        transferRepository.save(persisted);
        Transfer updated = transferRepository.findById(transfer.getId());

        assertNotNull(initialVersion);
        assertEquals(TransferStatus.COMPLETED, updated.getStatus());
        assertTrue(updated.getVersion() > initialVersion);
        assertEquals(persisted.getCreatedAt(), updated.getCreatedAt());
    }

    private Account account() {
        return new Account(
                UUID.randomUUID(), UUID.randomUUID(), new Money(new BigDecimal("100.00")),
                new Money(new BigDecimal("5000.00")));
    }
}
