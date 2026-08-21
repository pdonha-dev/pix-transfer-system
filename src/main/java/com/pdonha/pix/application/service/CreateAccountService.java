package com.pdonha.pix.application.service;

import com.pdonha.pix.application.dto.command.CreateAccountCommand;
import com.pdonha.pix.application.dto.result.AccountStateResult;
import com.pdonha.pix.domain.exception.CustomerNotFoundException;
import com.pdonha.pix.domain.model.Account;
import com.pdonha.pix.domain.model.Money;
import com.pdonha.pix.domain.port.AccountRepository;
import com.pdonha.pix.domain.port.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateAccountService {
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    public CreateAccountService(CustomerRepository customerRepository,
                                AccountRepository accountRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public AccountStateResult execute(CreateAccountCommand command) {
        if (customerRepository.findById(command.customerId()) == null) {
            throw new CustomerNotFoundException("Customer not found: " + command.customerId());
        }
        Account account = new Account(
                UUID.randomUUID(),
                command.customerId(),
                new Money(command.initialBalance()),
                new Money(command.dailyLimit())
        );
        accountRepository.save(account);
        return AccountStateResult.from(account, java.util.List.of());
    }
}
