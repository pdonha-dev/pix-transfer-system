package com.pdonha.pix.application.service;

import com.pdonha.pix.application.dto.result.AccountStateResult;
import com.pdonha.pix.domain.exception.AccountNotFoundException;
import com.pdonha.pix.domain.model.Account;
import com.pdonha.pix.domain.port.AccountRepository;
import com.pdonha.pix.domain.port.PixKeyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetAccountStateService {
    private final AccountRepository accountRepository;
    private final PixKeyRepository pixKeyRepository;

    public GetAccountStateService(AccountRepository accountRepository,
                                  PixKeyRepository pixKeyRepository) {
        this.accountRepository = accountRepository;
        this.pixKeyRepository = pixKeyRepository;
    }

    @Transactional(readOnly = true)
    public AccountStateResult execute(UUID accountId) {
        Account account = accountRepository.findById(accountId);
        if (account == null) {
            throw new AccountNotFoundException("Account not found: " + accountId);
        }
        return AccountStateResult.from(account, pixKeyRepository.findAllByAccountId(accountId));
    }
}
