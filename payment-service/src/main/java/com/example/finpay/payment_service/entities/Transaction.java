package com.example.finpay.payment_service.entities;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import com.example.finpay.payment_service.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.Instant;

@Container(containerName = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    private String id;

    @PartitionKey
    private String sourceAccountId;
    private String destinationAccountId;
    private BigDecimal amount;
    private TransactionStatus transactionStatus;
    private String idempotencyKey;
    private String description;
    private Instant createdAt;
    private Instant processedAt;


}
