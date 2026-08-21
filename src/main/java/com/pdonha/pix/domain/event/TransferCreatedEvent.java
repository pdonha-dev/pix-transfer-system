package com.pdonha.pix.domain.event;

import com.pdonha.pix.domain.model.Money;
import java.util.UUID;

public class TransferCreatedEvent extends DomainEvent {
    private final UUID payerAccountId;
    private final UUID payeeAccountId;
    private final Money amount;
    private final String createdBy;

    public TransferCreatedEvent(UUID transferId, UUID payerAccountId, UUID payeeAccountId, 
                               Money amount, String createdBy) {
        super(transferId, "Transfer", 1);
        this.payerAccountId = payerAccountId;
        this.payeeAccountId = payeeAccountId;
        this.amount = amount;
        this.createdBy = createdBy;
    }

    @Override
    public String getEventType() {
        return "TransferCreated";
    }

    public UUID getPayerAccountId() {
        return payerAccountId;
    }

    public UUID getPayeeAccountId() {
        return payeeAccountId;
    }

    public Money getAmount() {
        return amount;
    }

    public String getCreatedBy() {
        return createdBy;
    }
}
