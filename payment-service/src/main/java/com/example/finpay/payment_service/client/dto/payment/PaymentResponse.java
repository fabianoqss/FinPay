package com.example.finpay.payment_service.client.dto.payment;

import com.example.finpay.payment_service.entities.Transaction;
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
        Instant createdAt,
        Instant processedAt
) {

    public static PaymentResponse from(Transaction transaction) {
       return new PaymentResponse(
               transaction.getId(),
               transaction.getSourceAccountId(),
               transaction.getDestinationAccountId(),
               transaction.getAmount(),
               transaction.getTransactionStatus(),
               transaction.getBalanceOperation(),
               transaction.getCreatedAt(),
               transaction.getProcessedAt()
       );

   }
}
