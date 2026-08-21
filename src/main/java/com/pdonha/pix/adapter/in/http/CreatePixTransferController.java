package com.pdonha.pix.adapter.in.http;

import com.pdonha.pix.adapter.in.http.request.CreatePixTransferRequest;
import com.pdonha.pix.adapter.in.http.response.CreatePixTransferResponse;
import com.pdonha.pix.application.dto.command.CreatePixTransferCommand;
import com.pdonha.pix.application.dto.result.TransferResult;
import com.pdonha.pix.application.service.IdempotencyService;
import com.pdonha.pix.domain.exception.IdempotencyKeyInvalidException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pix-transfers")
@Tag(name = "PIX Transfers", description = "APIs for managing PIX instant transfers")
public class CreatePixTransferController {

    private final IdempotencyService idempotencyService;

    public CreatePixTransferController(IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
    }

    @PostMapping
    @Operation(
        summary = "Create a new PIX transfer",
        description = "Records a PIX transfer between accounts identified by PIX keys. Transfer starts in PENDING status. Idempotency-Key prevents duplicate balance movements and rejects reuse with a different payload."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Transfer created successfully",
            content = @Content(schema = @Schema(implementation = CreatePixTransferResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request body (validation failed on originPixKey, destinationPixKey, or amount) or missing Idempotency-Key header"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "PIX key not found (either origin or destination account does not exist)"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflict - Idempotency-Key is still processing from previous request"
        ),
        @ApiResponse(
            responseCode = "503",
            description = "Transfer authorization dependency is temporarily unavailable"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public ResponseEntity<CreatePixTransferResponse> create(
            @RequestHeader(value = "Idempotency-Key", required = true) String idempotencyKey,
            @Valid @RequestBody CreatePixTransferRequest request) {

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IdempotencyKeyInvalidException("Idempotency key cannot be blank");
        }

        CreatePixTransferCommand command = new CreatePixTransferCommand(
                request.getOriginPixKey(),
                request.getDestinationPixKey(),
                request.getAmount()
        );

        TransferResult result = idempotencyService.executeWithIdempotency(idempotencyKey, command);

        CreatePixTransferResponse response = new CreatePixTransferResponse(
                result.getTransferId(),
                result.getStatus(),
                result.getAmount().getAmount(),
                result.getCreatedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
