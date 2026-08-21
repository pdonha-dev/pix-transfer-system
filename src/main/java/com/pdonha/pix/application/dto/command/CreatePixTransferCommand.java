package com.pdonha.pix.application.dto.command;

import java.math.BigDecimal;

public class CreatePixTransferCommand {
    private final String originPixKey;
    private final String destinationPixKey;
    private final BigDecimal amount;

    public CreatePixTransferCommand(String originPixKey, String destinationPixKey, BigDecimal amount) {
        this.originPixKey = originPixKey;
        this.destinationPixKey = destinationPixKey;
        this.amount = amount;
    }

    public String getOriginPixKey() {
        return originPixKey;
    }

    public String getDestinationPixKey() {
        return destinationPixKey;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
