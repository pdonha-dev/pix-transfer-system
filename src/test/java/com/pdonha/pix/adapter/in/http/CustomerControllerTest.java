package com.pdonha.pix.adapter.in.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdonha.pix.adapter.in.http.request.CreateCustomerRequest;
import com.pdonha.pix.application.dto.result.CustomerResult;
import com.pdonha.pix.application.service.CreateCustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CustomerController.class, properties = "pix.test-setup.enabled=true")
class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CreateCustomerService createCustomerService;

    @Test
    void shouldCreateCustomer() throws Exception {
        UUID customerId = UUID.randomUUID();
        when(createCustomerService.execute(any()))
                .thenReturn(new CustomerResult(customerId, "Ana Silva", "12345678900"));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCustomerRequest("Ana Silva", "12345678900"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.cpf").value("12345678900"));
    }

    @Test
    void shouldRejectInvalidCpf() throws Exception {
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCustomerRequest("Ana Silva", "123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldRejectMissingCpf() throws Exception {
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ana Silva\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldRejectCustomerNameLongerThanDatabaseLimit() throws Exception {
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCustomerRequest("a".repeat(256), "12345678900"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"));
    }
}
