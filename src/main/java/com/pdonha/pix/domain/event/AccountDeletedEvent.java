package com.pdonha.pix.domain.event;

import com.pdonha.pix.domain.model.Money;
import java.util.UUID;

public class AccountDeletedEvent extends DomainEvent {
    private final String deletedBy;

    public AccountDeletedEvent(UUID accountId, int version, String deletedBy) {
        super(accountId, "Account", version);
        this.deletedBy = deletedBy;
    }

    @Override
    public String getEventType() {
        return "AccountDeleted";
    }

    public String getDeletedBy() {
        return deletedBy;
    }
}
