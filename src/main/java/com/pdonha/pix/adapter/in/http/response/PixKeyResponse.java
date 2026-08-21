package com.pdonha.pix.adapter.in.http.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pdonha.pix.application.dto.result.PixKeyResult;
import com.pdonha.pix.domain.model.PixKeyType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "PIX key details")
public record PixKeyResponse(
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000") UUID id,
        @JsonProperty("account_id")
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000") UUID accountId,
        @Schema(example = "EMAIL") PixKeyType type,
        @Schema(example = "ana@example.com") String value,
        boolean active
) {
    public static PixKeyResponse from(PixKeyResult result) {
        return new PixKeyResponse(result.id(), result.accountId(), result.type(),
                result.value(), result.active());
    }
}
