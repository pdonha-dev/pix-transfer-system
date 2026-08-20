package com.pdonha.pix.domain.port;

import com.pdonha.pix.domain.model.Account;

import java.util.UUID;

public interface AccountRepository {
    Account findById(UUID id);
    void save(Account account);
}
