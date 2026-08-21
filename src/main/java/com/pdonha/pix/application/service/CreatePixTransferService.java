package com.pdonha.pix.application.service;

import com.pdonha.pix.application.dto.command.CreatePixTransferCommand;
import com.pdonha.pix.application.dto.result.TransferResult;
import com.pdonha.pix.domain.exception.AccountNotFoundException;
import com.pdonha.pix.domain.exception.PixKeyNotFoundException;
import com.pdonha.pix.domain.model.Account;
import com.pdonha.pix.domain.model.Money;
import com.pdonha.pix.domain.model.LedgerEntry;
import com.pdonha.pix.domain.model.PixKey;
import com.pdonha.pix.domain.model.Transfer;
import com.pdonha.pix.domain.port.AccountRepository;
import com.pdonha.pix.domain.port.DomainEventRepository;
import com.pdonha.pix.domain.port.LedgerEntryRepository;
import com.pdonha.pix.domain.port.PixKeyRepository;
import com.pdonha.pix.domain.port.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreatePixTransferService {

    private final AccountRepository accountRepository;
    private final PixKeyRepository pixKeyRepository;
    private final TransferRepository transferRepository;
    private final DomainEventRepository domainEventRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public CreatePixTransferService(
            AccountRepository accountRepository,
            PixKeyRepository pixKeyRepository,
            TransferRepository transferRepository,
            DomainEventRepository domainEventRepository,
            LedgerEntryRepository ledgerEntryRepository) {
        this.accountRepository = accountRepository;
        this.pixKeyRepository = pixKeyRepository;
        this.transferRepository = transferRepository;
        this.domainEventRepository = domainEventRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Transactional
    public TransferResult execute(CreatePixTransferCommand cmd) {
        return execute(cmd, UUID.randomUUID());
    }

    @Transactional
    public TransferResult execute(CreatePixTransferCommand cmd, UUID transferId) {
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

        accountRepository.save(originAccount);
        accountRepository.save(destAccount);

        Transfer transfer = new Transfer(
                transferId,
                originAccount.getId(),
                destAccount.getId(),
                new Money(cmd.getAmount())
        );

        transferRepository.save(transfer);
        ledgerEntryRepository.save(LedgerEntry.debit(transfer.getId(), originAccount, transfer.getAmount()));
        ledgerEntryRepository.save(LedgerEntry.credit(transfer.getId(), destAccount, transfer.getAmount()));
        transfer.drainPendingEvents().forEach(domainEventRepository::append);

        return new TransferResult(
                transfer.getId(),
                transfer.getStatus(),
                transfer.getAmount(),
                transfer.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public TransferResult getTransferResult(UUID transferId) {
        Transfer transfer = transferRepository.findById(transferId);
        if (transfer == null) {
            throw new AccountNotFoundException("Transfer not found: " + transferId);
        }

        return new TransferResult(
                transfer.getId(),
                transfer.getStatus(),
                transfer.getAmount(),
                transfer.getCreatedAt()
        );
    }
}