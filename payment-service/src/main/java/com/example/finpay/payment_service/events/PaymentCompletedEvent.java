package com.example.finpay.payment_service.events;

import com.example.finpay.payment_service.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentCompletedEvent(
        String transactionId,
        String sourceAccountId,
        String destinationAccountId,
        BigDecimal amount,
        TransactionStatus status,
        Instant occurredAt
) {}
