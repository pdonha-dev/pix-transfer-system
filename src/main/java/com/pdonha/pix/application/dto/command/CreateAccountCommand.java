package com.pdonha.pix.application.dto.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateAccountCommand(UUID customerId, BigDecimal initialBalance,
                                   BigDecimal dailyLimit) {
}
