package com.pdonha.pix.domain.port;

import com.pdonha.pix.domain.model.LedgerEntry;

import java.util.List;
import java.util.UUID;

public interface LedgerEntryRepository {
    void save(LedgerEntry entry);
    List<LedgerEntry> findByTransferId(UUID transferId);
}
