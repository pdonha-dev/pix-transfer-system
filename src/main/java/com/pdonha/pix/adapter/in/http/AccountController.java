package com.pdonha.pix.adapter.in.http;

import com.pdonha.pix.adapter.in.http.request.CreateAccountRequest;
import com.pdonha.pix.adapter.in.http.request.DepositFundsRequest;
import com.pdonha.pix.adapter.in.http.response.AccountBalanceResponse;
import com.pdonha.pix.adapter.in.http.response.AccountStateResponse;
import com.pdonha.pix.application.dto.command.CreateAccountCommand;
import com.pdonha.pix.application.dto.command.DepositFundsCommand;
import com.pdonha.pix.application.service.CreateAccountService;
import com.pdonha.pix.application.service.DepositFundsService;
import com.pdonha.pix.application.service.GetAccountStateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@ConditionalOnProperty(name = "pix.test-setup.enabled", havingValue = "true")
@Tag(name = "Test Setup", description = "Resources for manually preparing PIX transfer scenarios")
public class AccountController {
    private final CreateAccountService createAccountService;
    private final DepositFundsService depositFundsService;
    private final GetAccountStateService getAccountStateService;

    public AccountController(CreateAccountService createAccountService,
                             DepositFundsService depositFundsService,
                             GetAccountStateService getAccountStateService) {
        this.createAccountService = createAccountService;
        this.depositFundsService = depositFundsService;
        this.getAccountStateService = getAccountStateService;
    }

    @PostMapping
    @Operation(summary = "Create an account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<AccountStateResponse> create(
            @Valid @RequestBody CreateAccountRequest request) {
        AccountStateResponse response = AccountStateResponse.from(createAccountService.execute(
                new CreateAccountCommand(
                        request.getCustomerId(),
                        request.getInitialBalance(),
                        request.getDailyLimit()
                )
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{accountId}/deposits")
    @Operation(summary = "Add test funds to an account",
            description = "Manual test setup only. A production funding flow must be authenticated and backed by a settlement source.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Funds added"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "409", description = "Account blocked")
    })
    public AccountBalanceResponse deposit(@PathVariable UUID accountId,
                                          @Valid @RequestBody DepositFundsRequest request) {
        return AccountBalanceResponse.from(depositFundsService.execute(
                new DepositFundsCommand(accountId, request.getAmount())
        ));
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get current account state")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account state returned"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public AccountStateResponse getById(@PathVariable UUID accountId) {
        return AccountStateResponse.from(getAccountStateService.execute(accountId));
    }
}
