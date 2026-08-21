package com.pdonha.pix.adapter.in.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdonha.pix.adapter.in.http.request.CreateAccountRequest;
import com.pdonha.pix.adapter.in.http.request.DepositFundsRequest;
import com.pdonha.pix.application.dto.result.AccountBalanceResult;
import com.pdonha.pix.application.dto.result.AccountStateResult;
import com.pdonha.pix.application.service.CreateAccountService;
import com.pdonha.pix.application.service.DepositFundsService;
import com.pdonha.pix.application.service.GetAccountStateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AccountController.class, properties = "pix.test-setup.enabled=true")
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CreateAccountService createAccountService;
    @MockBean
    private DepositFundsService depositFundsService;
    @MockBean
    private GetAccountStateService getAccountStateService;

    @Test
    void shouldCreateAccount() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        when(createAccountService.execute(any())).thenReturn(account(accountId, customerId, "0.00"));

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateAccountRequest(customerId, BigDecimal.ZERO,
                                        new BigDecimal("5000.00")))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(accountId.toString()))
                .andExpect(jsonPath("$.balance").value(0.00));
    }

    @Test
    void shouldDepositFunds() throws Exception {
        UUID accountId = UUID.randomUUID();
        when(depositFundsService.execute(any())).thenReturn(new AccountBalanceResult(
                accountId, new BigDecimal("1000.00"), LocalDateTime.now()));

        mockMvc.perform(post("/api/v1/accounts/{accountId}/deposits", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DepositFundsRequest(new BigDecimal("1000.00")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account_id").value(accountId.toString()))
                .andExpect(jsonPath("$.balance").value(1000.00));
    }

    @Test
    void shouldGetAccountState() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        when(getAccountStateService.execute(accountId))
                .thenReturn(account(accountId, customerId, "450.00"));

        mockMvc.perform(get("/api/v1/accounts/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId.toString()))
                .andExpect(jsonPath("$.balance").value(450.00))
                .andExpect(jsonPath("$.pix_keys").isArray());
    }

    @Test
    void shouldRejectNegativeDeposit() throws Exception {
        mockMvc.perform(post("/api/v1/accounts/{accountId}/deposits", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DepositFundsRequest(new BigDecimal("-1.00")))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"));
    }

    private AccountStateResult account(UUID accountId, UUID customerId, String balance) {
        LocalDateTime now = LocalDateTime.now();
        return new AccountStateResult(
                accountId, customerId, new BigDecimal(balance), new BigDecimal("5000.00"),
                BigDecimal.ZERO, true, now, now, List.of()
        );
    }
}
