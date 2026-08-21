package com.pdonha.pix.application.dto.command;

import com.pdonha.pix.domain.model.PixKeyType;

import java.util.UUID;

public record CreatePixKeyCommand(UUID accountId, PixKeyType type, String value) {
}
