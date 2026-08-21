package com.pdonha.pix.adapter.in.http;

import com.pdonha.pix.adapter.in.http.request.CreatePixKeyRequest;
import com.pdonha.pix.adapter.in.http.response.PixKeyResponse;
import com.pdonha.pix.application.dto.command.CreatePixKeyCommand;
import com.pdonha.pix.application.service.CreatePixKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pix-keys")
@ConditionalOnProperty(name = "pix.test-setup.enabled", havingValue = "true")
@Tag(name = "Test Setup", description = "Resources for manually preparing PIX transfer scenarios")
public class PixKeyController {
    private final CreatePixKeyService createPixKeyService;

    public PixKeyController(CreatePixKeyService createPixKeyService) {
        this.createPixKeyService = createPixKeyService;
    }

    @PostMapping
    @Operation(summary = "Register a PIX key")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "PIX key created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "409", description = "PIX key already registered or account blocked")
    })
    public ResponseEntity<PixKeyResponse> create(@Valid @RequestBody CreatePixKeyRequest request) {
        PixKeyResponse response = PixKeyResponse.from(createPixKeyService.execute(
                new CreatePixKeyCommand(request.getAccountId(), request.getType(), request.getValue())
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
