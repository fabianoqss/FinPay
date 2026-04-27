package com.example.finpay.payment_service.client.dto.payment;

import java.math.BigDecimal;

public record PaymentRequest(
    String originatingAccount,
    String destinationAccount,
    BigDecimal value

) {
}
