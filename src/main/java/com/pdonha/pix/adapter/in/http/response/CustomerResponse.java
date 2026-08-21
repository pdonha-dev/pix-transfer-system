package com.pdonha.pix.adapter.in.http.response;

import com.pdonha.pix.application.dto.result.CustomerResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Customer details")
public record CustomerResponse(
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000") UUID id,
        @Schema(example = "Ana Silva") String name,
        @Schema(example = "12345678900") String cpf
) {
    public static CustomerResponse from(CustomerResult result) {
        return new CustomerResponse(result.id(), result.name(), result.cpf());
    }
}
