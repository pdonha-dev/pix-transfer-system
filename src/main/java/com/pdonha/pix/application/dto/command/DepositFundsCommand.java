package com.pdonha.pix.application.dto.command;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositFundsCommand(UUID accountId, BigDecimal amount) {
}
