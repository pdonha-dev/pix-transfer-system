package com.pdonha.pix.domain.port;

import com.pdonha.pix.domain.model.Money;
import com.pdonha.pix.domain.model.TransferAuthorizationDecision;

import java.util.UUID;

public interface TransferAuthorizationPort {
    TransferAuthorizationDecision authorize(UUID transferId, String originPixKey,
                                            String destinationPixKey, Money amount);
}
