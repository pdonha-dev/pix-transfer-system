package com.pdonha.pix.adapter.in.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Request to create an active account")
public class CreateAccountRequest {
    @NotNull
    @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID customerId;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 17, fraction = 2)
    @Schema(example = "0.00")
    private BigDecimal initialBalance;

    @NotNull
    @Positive
    @Digits(integer = 17, fraction = 2)
    @Schema(example = "5000.00")
    private BigDecimal dailyLimit;

    public CreateAccountRequest() {
    }

    public CreateAccountRequest(UUID customerId, BigDecimal initialBalance,
                                BigDecimal dailyLimit) {
        this.customerId = customerId;
        this.initialBalance = initialBalance;
        this.dailyLimit = dailyLimit;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(BigDecimal dailyLimit) {
        this.dailyLimit = dailyLimit;
    }
}
