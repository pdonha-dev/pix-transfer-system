package com.pdonha.pix.adapter.in.http;

import com.pdonha.pix.adapter.in.http.request.CreatePixTransferRequest;
import com.pdonha.pix.adapter.in.http.response.CreatePixTransferResponse;
import com.pdonha.pix.application.dto.command.CreatePixTransferCommand;
import com.pdonha.pix.application.dto.result.TransferResult;
import com.pdonha.pix.application.service.CreatePixTransferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pix-transfers")
public class CreatePixTransferController {

    private final CreatePixTransferService createPixTransferService;

    public CreatePixTransferController(CreatePixTransferService createPixTransferService) {
        this.createPixTransferService = createPixTransferService;
    }

    @PostMapping
    public ResponseEntity<CreatePixTransferResponse> create(@Valid @RequestBody CreatePixTransferRequest request) {
        CreatePixTransferCommand command = new CreatePixTransferCommand(
                request.getOriginPixKey(),
                request.getDestinationPixKey(),
                request.getAmount()
        );

        TransferResult result = createPixTransferService.execute(command);

        CreatePixTransferResponse response = new CreatePixTransferResponse(
                result.getTransferId(),
                result.getStatus(),
                result.getAmount().getAmount(),
                result.getCreatedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
