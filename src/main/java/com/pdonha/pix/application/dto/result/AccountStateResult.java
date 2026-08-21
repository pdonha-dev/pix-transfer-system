package com.pdonha.pix.application.dto.result;

import com.pdonha.pix.domain.model.Account;
import com.pdonha.pix.domain.model.PixKey;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AccountStateResult(UUID id, UUID customerId, BigDecimal balance,
                                 BigDecimal dailyLimit, BigDecimal dailyUsed,
                                 boolean active, LocalDateTime createdAt,
                                 LocalDateTime updatedAt, List<PixKeyResult> pixKeys) {
    public static AccountStateResult from(Account account, List<PixKey> pixKeys) {
        return new AccountStateResult(
                account.getId(),
                account.getCustomerId(),
                account.getBalance().getAmount(),
                account.getDailyLimit().getAmount(),
                account.getDailyUsed().getAmount(),
                account.isActive(),
                account.getCreatedAt(),
                account.getUpdatedAt(),
                pixKeys.stream()
                        .map(pixKey -> new PixKeyResult(
                                pixKey.getId(),
                                pixKey.getAccountId(),
                                pixKey.getType(),
                                pixKey.getValue(),
                                pixKey.isActive()
                        ))
                        .toList()
        );
    }
}
