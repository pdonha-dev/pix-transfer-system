package com.pdonha.pix.application.dto.result;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountBalanceResult(UUID accountId, BigDecimal balance,
                                   LocalDateTime updatedAt) {
}
