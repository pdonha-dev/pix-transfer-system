package com.pdonha.pix.adapter.out.persistence.repository;

import com.pdonha.pix.domain.model.Account;
import com.pdonha.pix.domain.model.Money;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class AccountRepositoryIT {

    @Autowired
    private JpaAccountRepository repository;
    @Autowired
    private TransactionTemplate transactions;
    @Autowired
    private JdbcTemplate jdbc;

    @AfterEach
    @BeforeEach
    void cleanUp() {
        jdbc.update("DELETE FROM event_store");
        jdbc.update("DELETE FROM idempotency_keys");
        jdbc.update("DELETE FROM ledger_entries");
        jdbc.update("DELETE FROM transfers");
        jdbc.update("DELETE FROM pix_keys");
        jdbc.update("DELETE FROM accounts");
    }

    @Test
    void shouldPreserveCompleteStateAcrossPersistenceRoundTrip() {
        Account account = account(new BigDecimal("1000.00"));
        account.withdraw(new Money(new BigDecimal("125.00")));
        account.block();

        repository.save(account);
        Account loaded = repository.findById(account.getId());

        assertNotNull(loaded.getVersion());
        assertEquals(new BigDecimal("875.00"), loaded.getBalance().getAmount());
        assertEquals(new BigDecimal("125.00"), loaded.getDailyUsed().getAmount());
        assertEquals(false, loaded.isActive());
        assertEquals(account.getCreatedAt().truncatedTo(ChronoUnit.MILLIS),
                loaded.getCreatedAt().truncatedTo(ChronoUnit.MILLIS));
        assertEquals("system", jdbc.queryForObject(
                "SELECT created_by FROM accounts WHERE id = ?", String.class, account.getId()));
    }

    @Test
    void shouldRejectStaleAccountUpdate() {
        Account account = account(new BigDecimal("1000.00"));
        repository.save(account);
        Account firstCopy = transactions.execute(status -> repository.findById(account.getId()));
        Account staleCopy = transactions.execute(status -> repository.findById(account.getId()));

        firstCopy.deposit(new Money(BigDecimal.ONE));
        transactions.executeWithoutResult(status -> repository.save(firstCopy));
        staleCopy.deposit(new Money(BigDecimal.ONE));

        assertThrows(OptimisticLockingFailureException.class,
                () -> transactions.executeWithoutResult(status -> repository.save(staleCopy)));
    }

    @Test
    void shouldAllowOnlyOneConcurrentDebitAgainstSameVersion() throws Exception {
        Account account = account(new BigDecimal("1000.00"));
        repository.save(account);
        CyclicBarrier barrier = new CyclicBarrier(2);

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            List<Future<Boolean>> attempts = List.of(
                    executor.submit(() -> debitInTransaction(account.getId(), barrier)),
                    executor.submit(() -> debitInTransaction(account.getId(), barrier))
            );

            long successes = attempts.stream().filter(this::completedSuccessfully).count();

            assertEquals(1, successes);
        }

        Account loaded = repository.findById(account.getId());
        assertEquals(new BigDecimal("400.00"), loaded.getBalance().getAmount());
    }

    private boolean debitInTransaction(UUID accountId, CyclicBarrier barrier) {
        return Boolean.TRUE.equals(transactions.execute(status -> {
            Account loaded = repository.findById(accountId);
            try {
                barrier.await();
            } catch (Exception exception) {
                throw new IllegalStateException("Could not synchronize concurrent test", exception);
            }
            loaded.withdraw(new Money(new BigDecimal("600.00")));
            repository.save(loaded);
            return true;
        }));
    }

    private boolean completedSuccessfully(Future<Boolean> attempt) {
        try {
            return attempt.get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Concurrent test interrupted", exception);
        } catch (ExecutionException exception) {
            assertTrue(exception.getCause() instanceof OptimisticLockingFailureException);
            return false;
        }
    }

    private Account account(BigDecimal balance) {
        return new Account(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new Money(balance),
                new Money(new BigDecimal("5000.00"))
        );
    }
}
