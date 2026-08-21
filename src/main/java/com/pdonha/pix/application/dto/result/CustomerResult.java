package com.pdonha.pix.application.dto.result;

import java.util.UUID;

public record CustomerResult(UUID id, String name, String cpf) {
}
