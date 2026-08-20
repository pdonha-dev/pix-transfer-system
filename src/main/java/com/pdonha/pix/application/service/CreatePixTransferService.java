package com.pdonha.pix.application.service;

import com.pdonha.pix.application.dto.command.CreatePixTransferCommand;
import com.pdonha.pix.application.dto.result.TransferResult;
import com.pdonha.pix.domain.exception.AccountNotFoundException;
import com.pdonha.pix.domain.exception.PixKeyNotFoundException;
import com.pdonha.pix.domain.model.Account;
import com.pdonha.pix.domain.model.Money;
import com.pdonha.pix.domain.model.PixKey;
import com.pdonha.pix.domain.model.Transfer;
import com.pdonha.pix.domain.port.AccountRepository;
import com.pdonha.pix.domain.port.PixKeyRepository;
import com.pdonha.pix.domain.port.TransferRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreatePixTransferService {

    private final AccountRepository accountRepository;
    private final PixKeyRepository pixKeyRepository;
    private final TransferRepository transferRepository;

    public CreatePixTransferService(
            AccountRepository accountRepository,
            PixKeyRepository pixKeyRepository,
            TransferRepository transferRepository) {
        this.accountRepository = accountRepository;
        this.pixKeyRepository = pixKeyRepository;
        this.transferRepository = transferRepository;
    }

    public TransferResult execute(CreatePixTransferCommand cmd) {
        PixKey originKey = pixKeyRepository.findByKey(cmd.getOriginPixKey());
        PixKey destKey = pixKeyRepository.findByKey(cmd.getDestinationPixKey());

        if (originKey == null || destKey == null) {
            throw new PixKeyNotFoundException("Origin or destination PixKey not found");
        }

        Account originAccount = accountRepository.findById(originKey.getAccountId());
        Account destAccount = accountRepository.findById(destKey.getAccountId());

        if(originAccount == null || destAccount == null) {
            throw new AccountNotFoundException("Origin or destination Account not found");
        }

        originAccount.withdraw(new Money(cmd.getAmount()));
        destAccount.deposit(new Money(cmd.getAmount()));

        Transfer transfer = new Transfer(
                UUID.randomUUID(),
                originAccount.getId(),
                destAccount.getId(),
                new Money(cmd.getAmount())
        );

        transferRepository.save(transfer);

        return new TransferResult(
                transfer.getId(),
                transfer.getStatus(),
                transfer.getAmount(),
                transfer.getCreatedAt()
        );
    }
}