package com.pdonha.pix.domain.port;

import com.pdonha.pix.domain.model.Transfer;

import java.util.UUID;

public interface TransferRepository {
    Transfer findById(UUID id);
    void save(Transfer transfer);
}
