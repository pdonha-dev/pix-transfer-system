package com.pdonha.pix.adapter.in.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Request to add funds to an account for manual testing")
public class DepositFundsRequest {
    @NotNull
    @Positive
    @Digits(integer = 17, fraction = 2)
    @Schema(example = "1000.00")
    private BigDecimal amount;

    public DepositFundsRequest() {
    }

    public DepositFundsRequest(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
