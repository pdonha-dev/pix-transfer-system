package com.pdonha.pix.adapter.out.authorization;

import com.pdonha.pix.domain.exception.TransferAuthorizationUnavailableException;
import com.pdonha.pix.domain.model.Money;
import com.pdonha.pix.domain.model.TransferAuthorizationDecision;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = ResilientTransferAuthorizationGatewayTest.TestApplication.class,
        properties = {
                "resilience4j.circuitbreaker.instances.transferAuthorization.slidingWindowSize=2",
                "resilience4j.circuitbreaker.instances.transferAuthorization.minimumNumberOfCalls=2",
                "resilience4j.circuitbreaker.instances.transferAuthorization.failureRateThreshold=50",
                "resilience4j.circuitbreaker.instances.transferAuthorization.waitDurationInOpenState=1s",
                "resilience4j.retry.instances.transferAuthorization.maxAttempts=2",
                "resilience4j.retry.instances.transferAuthorization.waitDuration=1ms",
                "resilience4j.retry.instances.transferAuthorization.retryExceptions[0]="
                        + "com.pdonha.pix.domain.exception.TransferAuthorizationUnavailableException",
                "resilience4j.retry.instances.transferAuthorization.ignoreExceptions[0]="
                        + "java.util.concurrent.TimeoutException",
                "resilience4j.timelimiter.instances.transferAuthorization.timeoutDuration=50ms",
                "resilience4j.timelimiter.instances.transferAuthorization.cancelRunningFuture=true",
                "resilience4j.thread-pool-bulkhead.instances.transferAuthorization.maxThreadPoolSize=1",
                "resilience4j.thread-pool-bulkhead.instances.transferAuthorization.coreThreadPoolSize=1",
                "resilience4j.thread-pool-bulkhead.instances.transferAuthorization.queueCapacity=1"
        }
)
class ResilientTransferAuthorizationGatewayTest {

    @Configuration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            DataSourceTransactionManagerAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class,
            FlywayAutoConfiguration.class
    })
    @Import(ResilientTransferAuthorizationGateway.class)
    static class TestApplication {
    }

    @Autowired
    private ResilientTransferAuthorizationGateway gateway;
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;
    @MockBean
    private TransferAuthorizationClient client;

    private UUID transferId;
    private Money amount;

    @BeforeEach
    void setUp() {
        transferId = UUID.randomUUID();
        amount = new Money(new BigDecimal("100.00"));
        circuitBreakerRegistry.circuitBreaker(ResilientTransferAuthorizationGateway.INSTANCE).reset();
    }

    @Test
    void shouldRetryTransientAuthorizationFailure() {
        TransferAuthorizationDecision approved = TransferAuthorizationDecision.approved("AUTH-1");
        when(client.authorize(any(), any(), any(), any()))
                .thenThrow(new TransferAuthorizationUnavailableException("temporary"))
                .thenReturn(approved);

        TransferAuthorizationDecision result = authorize().join();

        assertEquals(approved, result);
        verify(client, times(2)).authorize(any(), any(), any(), any());
    }

    @Test
    void shouldFailFastWhenCircuitIsOpen() {
        circuitBreakerRegistry.circuitBreaker(ResilientTransferAuthorizationGateway.INSTANCE)
                .transitionToOpenState();

        CompletionException exception = assertThrows(CompletionException.class, () -> authorize().join());

        assertInstanceOf(CallNotPermittedException.class, exception.getCause());
    }

    @Test
    void shouldTimeoutSlowAuthorization() {
        when(client.authorize(any(), any(), any(), any())).thenAnswer(invocation -> {
            Thread.sleep(Duration.ofMillis(200));
            return TransferAuthorizationDecision.approved("LATE");
        });

        CompletionException exception = assertThrows(CompletionException.class, () -> authorize().join());

        assertInstanceOf(TimeoutException.class, exception.getCause());
        verify(client, times(1)).authorize(any(), any(), any(), any());
    }

    @Test
    void shouldRejectCallWhenBulkheadIsFull() throws Exception {
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        when(client.authorize(any(), any(), any(), any())).thenAnswer(invocation -> {
            entered.countDown();
            assertTrue(release.await(2, TimeUnit.SECONDS));
            return TransferAuthorizationDecision.approved("AUTH-BULKHEAD");
        });

        CompletableFuture<TransferAuthorizationDecision> running = authorize();
        assertTrue(entered.await(1, TimeUnit.SECONDS));
        CompletableFuture<TransferAuthorizationDecision> queued = authorize();
        CompletionException exception = assertThrows(CompletionException.class, () -> authorize().join());
        release.countDown();

        assertInstanceOf(BulkheadFullException.class, exception.getCause());
        assertEquals("AUTH-BULKHEAD", running.join().authorizationCode());
        assertEquals("AUTH-BULKHEAD", queued.join().authorizationCode());
    }

    private CompletableFuture<TransferAuthorizationDecision> authorize() {
        return gateway.authorize(transferId, "12345678900", "client@example.com", amount);
    }
}
