package com.pdonha.pix.adapter.out.authorization;

import com.pdonha.pix.domain.model.Money;
import com.pdonha.pix.domain.model.TransferAuthorizationDecision;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LocalTransferAuthorizationClient implements TransferAuthorizationClient {
    @Override
    public TransferAuthorizationDecision authorize(UUID transferId, String originPixKey,
                                                   String destinationPixKey, Money amount) {
        return TransferAuthorizationDecision.approved("LOCAL-" + transferId);
    }
}
