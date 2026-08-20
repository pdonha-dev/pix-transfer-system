package com.pdonha.pix.adapter.in.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class CreatePixTransferRequest {
    @NotBlank(message = "Origin PIX key cannot be blank")
    private String originPixKey;

    @NotBlank(message = "Destination PIX key cannot be blank")
    private String destinationPixKey;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    public CreatePixTransferRequest() {}

    public CreatePixTransferRequest(String originPixKey, String destinationPixKey, BigDecimal amount) {
        this.originPixKey = originPixKey;
        this.destinationPixKey = destinationPixKey;
        this.amount = amount;
    }

    public String getOriginPixKey() {
        return originPixKey;
    }

    public void setOriginPixKey(String originPixKey) {
        this.originPixKey = originPixKey;
    }

    public String getDestinationPixKey() {
        return destinationPixKey;
    }

    public void setDestinationPixKey(String destinationPixKey) {
        this.destinationPixKey = destinationPixKey;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
