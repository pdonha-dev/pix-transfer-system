package com.pdonha.pix.domain.model;

import com.pdonha.pix.domain.exception.PixKeyInvalidException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PixKeyTest {

    private PixKey createPixKey() {
        return new PixKey(UUID.randomUUID(), UUID.randomUUID(), PixKeyType.CPF, "12345678901");
    }

    @Test
    void block_shouldDeactivatePixKey_whenPixKeyIsActive() {
        // Arrange
        PixKey pixKey = createPixKey();

        // Act
        pixKey.block();

        // Assert
        assertFalse(pixKey.isActive());
    }

    @Test
    void block_shouldThrowPixKeyInvalidException_whenPixKeyIsAlreadyBlocked() {
        // Arrange
        PixKey pixKey = createPixKey();
        pixKey.block();

        // Act & Assert
        assertThrows(PixKeyInvalidException.class, pixKey::block);
    }

    @Test
    void unblock_shouldActivatePixKey_whenPixKeyIsBlocked() {
        // Arrange
        PixKey pixKey = createPixKey();
        pixKey.block();

        // Act
        pixKey.unblock();

        // Assert
        assertTrue(pixKey.isActive());
    }

    @Test
    void unblock_shouldThrowPixKeyInvalidException_whenPixKeyIsAlreadyActive() {
        // Arrange
        PixKey pixKey = createPixKey();

        // Act & Assert
        assertThrows(PixKeyInvalidException.class, pixKey::unblock);
    }
}
