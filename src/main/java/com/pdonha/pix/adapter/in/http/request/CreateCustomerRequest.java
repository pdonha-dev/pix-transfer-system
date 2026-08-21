package com.pdonha.pix.adapter.in.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to create a customer for account ownership")
public class CreateCustomerRequest {
    @NotBlank(message = "Customer name cannot be blank")
    @Size(max = 255, message = "Customer name must not exceed 255 characters")
    @Schema(example = "Ana Silva")
    private String name;

    @NotNull(message = "CPF cannot be null")
    @Pattern(regexp = "\\d{11}", message = "CPF must contain exactly 11 digits")
    @Schema(example = "12345678900")
    private String cpf;

    public CreateCustomerRequest() {
    }

    public CreateCustomerRequest(String name, String cpf) {
        this.name = name;
        this.cpf = cpf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
