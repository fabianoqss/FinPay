package com.example.finpay.payment_service.client.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(
        String id,
        String userId,
        BigDecimal balance,
        AccountStatus status,
        Instant createdAt
) {
}
