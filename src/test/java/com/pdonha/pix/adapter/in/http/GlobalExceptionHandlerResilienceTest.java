package com.pdonha.pix.adapter.in.http;

import com.pdonha.pix.domain.exception.TransferAuthorizationDeniedException;
import com.pdonha.pix.domain.exception.TransferAuthorizationUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerResilienceTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturnServiceUnavailableForTransientAuthorizationFailure() {
        var response = handler.handleAuthorizationUnavailable(
                new TransferAuthorizationUnavailableException("temporarily unavailable"));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("2", response.getHeaders().getFirst("Retry-After"));
        assertEquals("TRANSFER_AUTHORIZATION_UNAVAILABLE",
                response.getBody().getProperties().get("error_code"));
    }

    @Test
    void shouldReturnUnprocessableEntityForAuthorizationDenial() {
        var response = handler.handleAuthorizationDenied(
                new TransferAuthorizationDeniedException("denied"));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("TRANSFER_AUTHORIZATION_DENIED",
                response.getBody().getProperties().get("error_code"));
    }
}
