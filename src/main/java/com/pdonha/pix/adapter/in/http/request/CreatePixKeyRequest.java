package com.pdonha.pix.adapter.in.http.request;

import com.pdonha.pix.domain.model.PixKeyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Request to register a PIX key for an account")
public class CreatePixKeyRequest {
    @NotNull
    @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID accountId;

    @NotNull
    @Schema(example = "EMAIL")
    private PixKeyType type;

    @NotBlank
    @Size(max = 255)
    @Schema(example = "ana@example.com")
    private String value;

    public CreatePixKeyRequest() {
    }

    public CreatePixKeyRequest(UUID accountId, PixKeyType type, String value) {
        this.accountId = accountId;
        this.type = type;
        this.value = value;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public PixKeyType getType() {
        return type;
    }

    public void setType(PixKeyType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
