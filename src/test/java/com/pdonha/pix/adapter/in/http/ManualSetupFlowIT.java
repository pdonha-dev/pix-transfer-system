package com.pdonha.pix.adapter.in.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ManualSetupFlowIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        jdbc.update("DELETE FROM event_store");
        jdbc.update("DELETE FROM idempotency_keys");
        jdbc.update("DELETE FROM ledger_entries");
        jdbc.update("DELETE FROM transfers");
        jdbc.update("DELETE FROM pix_keys");
        jdbc.update("DELETE FROM accounts");
        jdbc.update("DELETE FROM customers");
    }

    @Test
    void shouldPrepareAndExecutePixTransferThroughHttpApi() throws Exception {
        UUID senderCustomerId = createCustomer("Ana Silva", "12345678900");
        UUID receiverCustomerId = createCustomer("Bruno Souza", "98765432100");
        UUID senderAccountId = createAccount(senderCustomerId);
        UUID receiverAccountId = createAccount(receiverCustomerId);

        deposit(senderAccountId, new BigDecimal("1000.00"));
        createPixKey(senderAccountId, "CPF", "12345678900");
        createPixKey(receiverAccountId, "EMAIL", "bruno@example.com");

        mockMvc.perform(post("/api/v1/pix-transfers")
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "originPixKey", "12345678900",
                                "destinationPixKey", "bruno@example.com",
                                "amount", new BigDecimal("150.00")
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.amount").value(150.00));

        assertAccountState(senderAccountId, "850.00", "150.00", "12345678900");
        assertAccountState(receiverAccountId, "150.00", "0.00", "bruno@example.com");
    }

    @Test
    void shouldReturnConflictForDuplicateCustomerCpf() throws Exception {
        createCustomer("Ana Silva", "12345678900");

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", "Ana Souza", "cpf", "12345678900"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error_code").value("DUPLICATE_RESOURCE"));
    }

    private UUID createCustomer(String name, String cpf) throws Exception {
        MvcResult response = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", name, "cpf", cpf))))
                .andExpect(status().isCreated())
                .andReturn();
        return UUID.fromString(body(response).get("id").asText());
    }

    private UUID createAccount(UUID customerId) throws Exception {
        MvcResult response = mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "customerId", customerId,
                                "initialBalance", BigDecimal.ZERO,
                                "dailyLimit", new BigDecimal("5000.00")
                        ))))
                .andExpect(status().isCreated())
                .andReturn();
        return UUID.fromString(body(response).get("id").asText());
    }

    private void deposit(UUID accountId, BigDecimal amount) throws Exception {
        mockMvc.perform(post("/api/v1/accounts/{accountId}/deposits", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("amount", amount))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(amount.doubleValue()));
    }

    private void createPixKey(UUID accountId, String type, String value) throws Exception {
        mockMvc.perform(post("/api/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "accountId", accountId,
                                "type", type,
                                "value", value
                        ))))
                .andExpect(status().isCreated());
    }

    private void assertAccountState(UUID accountId, String balance, String dailyUsed,
                                    String pixKey) throws Exception {
        mockMvc.perform(get("/api/v1/accounts/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(new BigDecimal(balance).doubleValue()))
                .andExpect(jsonPath("$.daily_used").value(new BigDecimal(dailyUsed).doubleValue()))
                .andExpect(jsonPath("$.pix_keys[0].value").value(pixKey));
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    private JsonNode body(MvcResult response) throws Exception {
        return objectMapper.readTree(response.getResponse().getContentAsString());
    }
}
