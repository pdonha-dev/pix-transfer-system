package com.pdonha.pix.adapter.out.authorization;

import com.pdonha.pix.domain.model.Money;
import com.pdonha.pix.domain.model.TransferAuthorizationDecision;

import java.util.UUID;

public interface TransferAuthorizationClient {
    TransferAuthorizationDecision authorize(UUID transferId, String originPixKey,
                                            String destinationPixKey, Money amount);
}
