package com.pdonha.pix.application.dto.result;

import com.pdonha.pix.domain.model.PixKeyType;

import java.util.UUID;

public record PixKeyResult(UUID id, UUID accountId, PixKeyType type, String value,
                           boolean active) {
}
