package com.pdonha.pix.adapter.in.http.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pdonha.pix.application.dto.result.AccountBalanceResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Updated account balance")
public record AccountBalanceResponse(
        @JsonProperty("account_id")
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000") UUID accountId,
        @Schema(example = "1000.00") BigDecimal balance,
        @JsonProperty("updated_at") LocalDateTime updatedAt
) {
    public static AccountBalanceResponse from(AccountBalanceResult result) {
        return new AccountBalanceResponse(result.accountId(), result.balance(), result.updatedAt());
    }
}
