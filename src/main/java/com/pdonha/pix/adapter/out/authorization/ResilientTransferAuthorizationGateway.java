package com.pdonha.pix.adapter.out.authorization;

import com.pdonha.pix.domain.model.Money;
import com.pdonha.pix.domain.model.TransferAuthorizationDecision;
import io.github.resilience4j.bulkhead.annotation.Bulkhead.Type;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
public class ResilientTransferAuthorizationGateway {
    static final String INSTANCE = "transferAuthorization";

    private final TransferAuthorizationClient client;

    public ResilientTransferAuthorizationGateway(TransferAuthorizationClient client) {
        this.client = client;
    }

    @Retry(name = INSTANCE)
    @CircuitBreaker(name = INSTANCE)
    @TimeLimiter(name = INSTANCE)
    @Bulkhead(name = INSTANCE, type = Type.THREADPOOL)
    public CompletableFuture<TransferAuthorizationDecision> authorize(
            UUID transferId, String originPixKey, String destinationPixKey, Money amount) {
        return CompletableFuture.completedFuture(
                client.authorize(transferId, originPixKey, destinationPixKey, amount));
    }
}
