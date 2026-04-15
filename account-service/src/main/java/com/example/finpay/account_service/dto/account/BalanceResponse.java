package com.example.finpay.account_service.dto.account;

import java.math.BigDecimal;
import java.time.Instant;

public record BalanceResponse(
        String accountId,
        BigDecimal balance,
        Instant consultedAt
) {
}
