package com.example.finpay.payment_service.client.dto.payment;

import com.example.finpay.payment_service.enums.BalanceOperation;
import com.example.finpay.payment_service.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        String id,
        String sourceAccountId,
        String destinationAccountId,
        BigDecimal amount,
        TransactionStatus transactionStatus,
        BalanceOperation balanceOperation,
        Instant create,
        Instant processedAt
) {
}
