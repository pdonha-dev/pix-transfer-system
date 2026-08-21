package com.pdonha.pix.adapter.in.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdonha.pix.adapter.in.http.request.CreatePixKeyRequest;
import com.pdonha.pix.application.dto.result.PixKeyResult;
import com.pdonha.pix.application.service.CreatePixKeyService;
import com.pdonha.pix.domain.model.PixKeyType;
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

@WebMvcTest(value = PixKeyController.class, properties = "pix.test-setup.enabled=true")
class PixKeyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CreatePixKeyService createPixKeyService;

    @Test
    void shouldCreatePixKey() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID pixKeyId = UUID.randomUUID();
        when(createPixKeyService.execute(any())).thenReturn(new PixKeyResult(
                pixKeyId, accountId, PixKeyType.EMAIL, "ana@example.com", true));

        mockMvc.perform(post("/api/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreatePixKeyRequest(accountId, PixKeyType.EMAIL, "ana@example.com"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(pixKeyId.toString()))
                .andExpect(jsonPath("$.account_id").value(accountId.toString()))
                .andExpect(jsonPath("$.type").value("EMAIL"));
    }

    @Test
    void shouldRejectMissingPixKeyType() throws Exception {
        mockMvc.perform(post("/api/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreatePixKeyRequest(UUID.randomUUID(), null, "ana@example.com"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldRejectPixKeyValueLongerThanDatabaseLimit() throws Exception {
        mockMvc.perform(post("/api/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreatePixKeyRequest(UUID.randomUUID(), PixKeyType.EMAIL,
                                        "a".repeat(256)))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"));
    }
}
