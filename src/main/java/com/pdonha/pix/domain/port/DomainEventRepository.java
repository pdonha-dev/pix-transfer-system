package com.pdonha.pix.domain.port;

import com.pdonha.pix.domain.event.DomainEvent;

public interface DomainEventRepository {
    void append(DomainEvent event);
}
