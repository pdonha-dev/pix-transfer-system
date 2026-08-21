package com.pdonha.pix.application.service;

import com.pdonha.pix.application.dto.command.CreatePixKeyCommand;
import com.pdonha.pix.application.dto.result.PixKeyResult;
import com.pdonha.pix.domain.exception.AccountBlockedException;
import com.pdonha.pix.domain.exception.AccountNotFoundException;
import com.pdonha.pix.domain.model.Account;
import com.pdonha.pix.domain.model.PixKey;
import com.pdonha.pix.domain.port.AccountRepository;
import com.pdonha.pix.domain.port.PixKeyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreatePixKeyService {
    private final AccountRepository accountRepository;
    private final PixKeyRepository pixKeyRepository;

    public CreatePixKeyService(AccountRepository accountRepository,
                               PixKeyRepository pixKeyRepository) {
        this.accountRepository = accountRepository;
        this.pixKeyRepository = pixKeyRepository;
    }

    @Transactional
    public PixKeyResult execute(CreatePixKeyCommand command) {
        Account account = accountRepository.findById(command.accountId());
        if (account == null) {
            throw new AccountNotFoundException("Account not found: " + command.accountId());
        }
        if (!account.isActive()) {
            throw new AccountBlockedException("Account is blocked", account.getId().toString());
        }
        PixKey pixKey = new PixKey(UUID.randomUUID(), account.getId(), command.type(), command.value());
        pixKeyRepository.save(pixKey);
        return new PixKeyResult(
                pixKey.getId(),
                pixKey.getAccountId(),
                pixKey.getType(),
                pixKey.getValue(),
                pixKey.isActive()
        );
    }
}
