package com.pdonha.pix.adapter.out.authorization;

import com.pdonha.pix.domain.exception.TransferAuthorizationUnavailableException;
import com.pdonha.pix.domain.model.Money;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResilientTransferAuthorizationAdapterTest {

    @Test
    void shouldTranslateOpenCircuitToDomainException() {
        ResilientTransferAuthorizationGateway gateway = mock(ResilientTransferAuthorizationGateway.class);
        CompletableFuture<com.pdonha.pix.domain.model.TransferAuthorizationDecision> failed =
                CompletableFuture.failedFuture(CallNotPermittedException.createCallNotPermittedException(
                        CircuitBreaker.ofDefaults("test")));
        when(gateway.authorize(any(), any(), any(), any())).thenReturn(failed);
        ResilientTransferAuthorizationAdapter adapter = new ResilientTransferAuthorizationAdapter(gateway);

        assertThrows(TransferAuthorizationUnavailableException.class, () -> adapter.authorize(
                UUID.randomUUID(), "origin", "destination", new Money(BigDecimal.ONE)));
    }
}
