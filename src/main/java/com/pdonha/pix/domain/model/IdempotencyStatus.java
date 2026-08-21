package com.pdonha.pix.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public enum IdempotencyStatus {
    PENDING,
    SUCCESS,
    FAILED
}
