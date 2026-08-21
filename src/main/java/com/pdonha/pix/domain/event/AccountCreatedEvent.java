package com.pdonha.pix.domain.event;

import com.pdonha.pix.domain.model.Money;
import java.util.UUID;

public class AccountCreatedEvent extends DomainEvent {
    private final UUID customerId;
    private final Money initialBalance;
    private final Money dailyLimit;
    private final String createdBy;

    public AccountCreatedEvent(UUID accountId, UUID customerId, Money initialBalance, 
                               Money dailyLimit, String createdBy) {
        super(accountId, "Account", 1);
        this.customerId = customerId;
        this.initialBalance = initialBalance;
        this.dailyLimit = dailyLimit;
        this.createdBy = createdBy;
    }

    @Override
    public String getEventType() {
        return "AccountCreated";
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public Money getInitialBalance() {
        return initialBalance;
    }

    public Money getDailyLimit() {
        return dailyLimit;
    }

    public String getCreatedBy() {
        return createdBy;
    }
}
