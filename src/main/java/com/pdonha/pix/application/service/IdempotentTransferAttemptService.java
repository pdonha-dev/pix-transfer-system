package com.pdonha.pix.application.service;

import com.pdonha.pix.application.dto.command.CreatePixTransferCommand;
import com.pdonha.pix.application.dto.result.TransferResult;
import com.pdonha.pix.domain.exception.InvalidIdempotencyKeyException;
import com.pdonha.pix.domain.model.IdempotencyKey;
import com.pdonha.pix.domain.port.IdempotencyKeyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class IdempotentTransferAttemptService {

    private final CreatePixTransferService createPixTransferService;
    private final IdempotencyKeyRepository idempotencyKeyRepository;

    public IdempotentTransferAttemptService(CreatePixTransferService createPixTransferService,
                                            IdempotencyKeyRepository idempotencyKeyRepository) {
        this.createPixTransferService = createPixTransferService;
        this.idempotencyKeyRepository = idempotencyKeyRepository;
    }

    @Transactional
    public TransferResult execute(String idempotencyKey, UUID transferId, CreatePixTransferCommand command) {
        TransferResult result = createPixTransferService.execute(command, transferId);
        IdempotencyKey record = idempotencyKeyRepository.findByKey(idempotencyKey)
                .orElseThrow(() -> new InvalidIdempotencyKeyException(
                        "Idempotency key not found during transfer: " + idempotencyKey));
        record.markSuccessful();
        idempotencyKeyRepository.save(record);
        return result;
    }
}
