package com.pdonha.pix.adapter.in.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Request to create a new PIX transfer")
public class CreatePixTransferRequest {
    @Schema(
        description = "Origin PIX key (CPF, Email, Phone, or Random UUID)",
        example = "12345678900"
    )
    @NotBlank(message = "Origin PIX key cannot be blank")
    private String originPixKey;

    @Schema(
        description = "Destination PIX key (CPF, Email, Phone, or Random UUID)",
        example = "user@example.com"
    )
    @NotBlank(message = "Destination PIX key cannot be blank")
    private String destinationPixKey;

    @Schema(
        description = "Transfer amount in BRL (Real). Minimum: 0.01",
        example = "100.00"
    )
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
