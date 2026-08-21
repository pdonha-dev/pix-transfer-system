package com.pdonha.pix.application.service;

import com.pdonha.pix.adapter.out.persistence.repository.JpaAccountRepository;
import com.pdonha.pix.adapter.out.persistence.repository.JpaEventStoreRepository;
import com.pdonha.pix.adapter.out.persistence.repository.JpaPixKeyRepository;
import com.pdonha.pix.adapter.out.persistence.repository.JpaLedgerEntryRepository;
import com.pdonha.pix.application.dto.command.CreatePixTransferCommand;
import com.pdonha.pix.application.dto.result.TransferResult;
import com.pdonha.pix.domain.exception.IdempotencyKeyConflictException;
import com.pdonha.pix.domain.model.Account;
import com.pdonha.pix.domain.model.IdempotencyStatus;
import com.pdonha.pix.domain.model.Money;
import com.pdonha.pix.domain.model.PixKey;
import com.pdonha.pix.domain.model.PixKeyType;
import com.pdonha.pix.domain.port.IdempotencyKeyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class IdempotencyServiceIT {

    @Autowired
    private IdempotencyService service;
    @Autowired
    private CreatePixTransferService transferService;
    @Autowired
    private JpaAccountRepository accountRepository;
    @Autowired
    private JpaPixKeyRepository pixKeyRepository;
    @Autowired
    private JpaEventStoreRepository eventStoreRepository;
    @Autowired
    private JpaLedgerEntryRepository ledgerEntryRepository;
    @Autowired
    private IdempotencyKeyRepository idempotencyKeyRepository;
    @Autowired
    private JdbcTemplate jdbc;

    private UUID payerId;
    private UUID payeeId;
    private CreatePixTransferCommand command;

    @BeforeEach
    void setUp() {
        jdbc.update("DELETE FROM event_store");
        jdbc.update("DELETE FROM idempotency_keys");
        jdbc.update("DELETE FROM ledger_entries");
        jdbc.update("DELETE FROM transfers");
        jdbc.update("DELETE FROM pix_keys");
        jdbc.update("DELETE FROM accounts");

        payerId = UUID.randomUUID();
        payeeId = UUID.randomUUID();
        accountRepository.save(account(payerId, new BigDecimal("1000.00")));
        accountRepository.save(account(payeeId, BigDecimal.ZERO));
        pixKeyRepository.save(new PixKey(UUID.randomUUID(), payerId, PixKeyType.CPF, "98765432100"));
        pixKeyRepository.save(new PixKey(UUID.randomUUID(), payeeId, PixKeyType.EMAIL, "idempotency@example.com"));
        command = new CreatePixTransferCommand(
                "98765432100", "idempotency@example.com", new BigDecimal("100.00"));
    }

    @Test
    void shouldReplaySuccessfulRequestWithoutMovingMoneyTwice() {
        String key = UUID.randomUUID().toString();

        TransferResult first = service.executeWithIdempotency(key, command);
        TransferResult replay = service.executeWithIdempotency(key, command);

        assertEquals(first.getTransferId(), replay.getTransferId());
        assertEquals(new BigDecimal("900.00"), accountRepository.findById(payerId).getBalance().getAmount());
        assertEquals(new BigDecimal("100.00"), accountRepository.findById(payeeId).getBalance().getAmount());
        assertEquals(1, eventStoreRepository.findByAggregateId(first.getTransferId()).size());
        assertEquals(2, ledgerEntryRepository.findByTransferId(first.getTransferId()).size());
        assertEquals(IdempotencyStatus.SUCCESS,
                idempotencyKeyRepository.findByKey(key).orElseThrow().getStatus());
    }

    @Test
    void shouldRejectReusedKeyWithDifferentPayload() {
        String key = UUID.randomUUID().toString();
        service.executeWithIdempotency(key, command);
        CreatePixTransferCommand changed = new CreatePixTransferCommand(
                "98765432100", "idempotency@example.com", new BigDecimal("200.00"));

        assertThrows(IdempotencyKeyConflictException.class,
                () -> service.executeWithIdempotency(key, changed));
    }

    @Test
    void shouldRollbackBalancesWhenTransferPersistenceFails() {
        UUID transferId = UUID.randomUUID();
        transferService.execute(command, transferId);
        BigDecimal payerBalance = accountRepository.findById(payerId).getBalance().getAmount();
        BigDecimal payeeBalance = accountRepository.findById(payeeId).getBalance().getAmount();

        assertThrows(RuntimeException.class, () -> transferService.execute(command, transferId));

        assertEquals(payerBalance, accountRepository.findById(payerId).getBalance().getAmount());
        assertEquals(payeeBalance, accountRepository.findById(payeeId).getBalance().getAmount());
        assertEquals(1, eventStoreRepository.findByAggregateId(transferId).size());
        assertEquals(2, ledgerEntryRepository.findByTransferId(transferId).size());
    }

    private Account account(UUID id, BigDecimal balance) {
        return new Account(
                id,
                UUID.randomUUID(),
                new Money(balance),
                new Money(new BigDecimal("5000.00"))
        );
    }
}
