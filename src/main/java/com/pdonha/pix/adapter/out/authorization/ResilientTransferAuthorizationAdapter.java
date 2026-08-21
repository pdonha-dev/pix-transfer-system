package com.pdonha.pix.adapter.out.authorization;

import com.pdonha.pix.domain.exception.TransferAuthorizationDeniedException;
import com.pdonha.pix.domain.exception.TransferAuthorizationUnavailableException;
import com.pdonha.pix.domain.model.Money;
import com.pdonha.pix.domain.model.TransferAuthorizationDecision;
import com.pdonha.pix.domain.port.TransferAuthorizationPort;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;

@Component
public class ResilientTransferAuthorizationAdapter implements TransferAuthorizationPort {
    private final ResilientTransferAuthorizationGateway gateway;

    public ResilientTransferAuthorizationAdapter(ResilientTransferAuthorizationGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public TransferAuthorizationDecision authorize(UUID transferId, String originPixKey,
                                                   String destinationPixKey, Money amount) {
        try {
            return gateway.authorize(transferId, originPixKey, destinationPixKey, amount).join();
        } catch (CompletionException exception) {
            throw translate(exception.getCause());
        } catch (CancellationException exception) {
            throw new TransferAuthorizationUnavailableException(
                    "Transfer authorization was cancelled", exception);
        }
    }

    private RuntimeException translate(Throwable cause) {
        if (cause instanceof TransferAuthorizationDeniedException denied) {
            return denied;
        }
        if (cause instanceof TransferAuthorizationUnavailableException unavailable) {
            return unavailable;
        }
        if (cause instanceof CallNotPermittedException
                || cause instanceof BulkheadFullException
                || cause instanceof TimeoutException) {
            return new TransferAuthorizationUnavailableException(
                    "Transfer authorization service is temporarily unavailable", cause);
        }
        return new TransferAuthorizationUnavailableException(
                "Transfer authorization failed unexpectedly", cause);
    }
}
