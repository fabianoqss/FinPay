package com.example.finpay.payment_service.client.dto.payment;

import java.math.BigDecimal;

public record PaymentRequest(
        String keyIdempotence,
        String userOriginatingId,
        String originatingAccount,
        String userDestinationId,
        String destinationAccount,
        BigDecimal value,
        String description
) {}
