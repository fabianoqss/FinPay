package com.example.finpay.payment_service.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        String keyIdempotence,
        String userOriginatingId,
        String originatingAccount,
        String userDestinationId,
        String destinationAccount,
        BigDecimal value,
        String description
) {
}
