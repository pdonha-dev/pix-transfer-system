package com.pdonha.pix.adapter.in.http.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pdonha.pix.application.dto.result.AccountStateResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Current account state and registered PIX keys")
public record AccountStateResponse(
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000") UUID id,
        @JsonProperty("customer_id") UUID customerId,
        @Schema(example = "1000.00") BigDecimal balance,
        @JsonProperty("daily_limit") BigDecimal dailyLimit,
        @JsonProperty("daily_used") BigDecimal dailyUsed,
        boolean active,
        @JsonProperty("created_at") LocalDateTime createdAt,
        @JsonProperty("updated_at") LocalDateTime updatedAt,
        @JsonProperty("pix_keys") List<PixKeyResponse> pixKeys
) {
    public static AccountStateResponse from(AccountStateResult result) {
        return new AccountStateResponse(
                result.id(),
                result.customerId(),
                result.balance(),
                result.dailyLimit(),
                result.dailyUsed(),
                result.active(),
                result.createdAt(),
                result.updatedAt(),
                result.pixKeys().stream()
                        .map(pixKey -> new PixKeyResponse(
                                pixKey.id(),
                                pixKey.accountId(),
                                pixKey.type(),
                                pixKey.value(),
                                pixKey.active()
                        ))
                        .toList()
        );
    }
}
