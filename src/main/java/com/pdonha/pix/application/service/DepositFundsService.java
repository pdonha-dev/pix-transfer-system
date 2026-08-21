package com.pdonha.pix.application.service;

import com.pdonha.pix.application.dto.command.DepositFundsCommand;
import com.pdonha.pix.application.dto.result.AccountBalanceResult;
import com.pdonha.pix.domain.exception.AccountNotFoundException;
import com.pdonha.pix.domain.model.Account;
import com.pdonha.pix.domain.model.Money;
import com.pdonha.pix.domain.port.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepositFundsService {
    private final AccountRepository accountRepository;

    public DepositFundsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public AccountBalanceResult execute(DepositFundsCommand command) {
        Account account = accountRepository.findById(command.accountId());
        if (account == null) {
            throw new AccountNotFoundException("Account not found: " + command.accountId());
        }
        account.deposit(new Money(command.amount()));
        accountRepository.save(account);
        return new AccountBalanceResult(
                account.getId(),
                account.getBalance().getAmount(),
                account.getUpdatedAt()
        );
    }
}
