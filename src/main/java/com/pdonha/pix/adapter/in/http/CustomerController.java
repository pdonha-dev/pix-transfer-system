package com.pdonha.pix.adapter.in.http;

import com.pdonha.pix.adapter.in.http.request.CreateCustomerRequest;
import com.pdonha.pix.adapter.in.http.response.CustomerResponse;
import com.pdonha.pix.application.dto.command.CreateCustomerCommand;
import com.pdonha.pix.application.service.CreateCustomerService;
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
@RequestMapping("/api/v1/customers")
@ConditionalOnProperty(name = "pix.test-setup.enabled", havingValue = "true")
@Tag(name = "Test Setup", description = "Resources for manually preparing PIX transfer scenarios")
public class CustomerController {
    private final CreateCustomerService createCustomerService;

    public CustomerController(CreateCustomerService createCustomerService) {
        this.createCustomerService = createCustomerService;
    }

    @PostMapping
    @Operation(summary = "Create a customer")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "CPF already registered")
    })
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest request) {
        CustomerResponse response = CustomerResponse.from(createCustomerService.execute(
                new CreateCustomerCommand(request.getName(), request.getCpf())
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
