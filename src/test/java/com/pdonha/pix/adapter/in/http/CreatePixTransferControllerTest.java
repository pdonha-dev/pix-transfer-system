package com.pdonha.pix.adapter.in.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdonha.pix.adapter.in.http.request.CreatePixTransferRequest;
import com.pdonha.pix.application.service.IdempotencyService;
import com.pdonha.pix.domain.exception.PixKeyNotFoundException;
import com.pdonha.pix.domain.model.Money;
import com.pdonha.pix.domain.model.TransferStatus;
import com.pdonha.pix.application.dto.result.TransferResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CreatePixTransferController.class)
@ActiveProfiles("test")
class CreatePixTransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IdempotencyService idempotencyService;

    @Test
    void shouldCreatePixTransferSuccessfully() throws Exception {
        UUID transferId = UUID.randomUUID();
        String idempotencyKey = UUID.randomUUID().toString();
        CreatePixTransferRequest request = new CreatePixTransferRequest(
                "12345678900",
                "user@example.com",
                new BigDecimal("100.00")
        );

        TransferResult mockResult = new TransferResult(
                transferId,
                TransferStatus.PENDING,
                new Money(new BigDecimal("100.00")),
                LocalDateTime.now()
        );

        Mockito.when(idempotencyService.executeWithIdempotency(
                Mockito.eq(idempotencyKey),
                Mockito.any()))
                .thenReturn(mockResult);

        mockMvc.perform(post("/api/v1/pix-transfers")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transfer_id").value(transferId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    void shouldReturn400WhenOriginPixKeyBlank() throws Exception {
        String idempotencyKey = UUID.randomUUID().toString();
        CreatePixTransferRequest request = new CreatePixTransferRequest(
                "",
                "user@example.com",
                new BigDecimal("100.00")
        );

        mockMvc.perform(post("/api/v1/pix-transfers")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenPixKeyNotFound() throws Exception {
        String idempotencyKey = UUID.randomUUID().toString();
        CreatePixTransferRequest request = new CreatePixTransferRequest(
                "nonexistent@example.com",
                "user@example.com",
                new BigDecimal("100.00")
        );

        Mockito.when(idempotencyService.executeWithIdempotency(
                Mockito.eq(idempotencyKey),
                Mockito.any()))
                .thenThrow(new PixKeyNotFoundException("PIX key not found"));

        mockMvc.perform(post("/api/v1/pix-transfers")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error_code").value("PIX_KEY_NOT_FOUND"))
                .andExpect(jsonPath("$.title").value("PIX key not found"))
                .andExpect(jsonPath("$.detail").value("PIX key not found"));
    }

    @Test
    void shouldReturn400WhenAmountNegative() throws Exception {
        String idempotencyKey = UUID.randomUUID().toString();
        CreatePixTransferRequest request = new CreatePixTransferRequest(
                "12345678900",
                "user@example.com",
                new BigDecimal("-100.00")
        );

        mockMvc.perform(post("/api/v1/pix-transfers")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
