package com.example.finpay.payment_service.services;

import com.example.finpay.payment_service.client.account.AccountClient;
import com.example.finpay.payment_service.client.account.AccountResponse;
import com.example.finpay.payment_service.client.account.AccountStatus;
import com.example.finpay.payment_service.client.account.UpdateBalanceRequest;
import com.example.finpay.payment_service.dto.PaymentRequest;
import com.example.finpay.payment_service.dto.PaymentResponse;
import com.example.finpay.payment_service.entities.Transaction;
import com.example.finpay.payment_service.enums.BalanceOperation;
import com.example.finpay.payment_service.enums.TransactionStatus;
import com.example.finpay.payment_service.events.PaymentCompletedEvent;
import com.example.finpay.payment_service.repositories.TransactionRepository;
import com.example.finpay.payment_service.exceptions.AccountBlockedException;
import com.example.finpay.payment_service.exceptions.InsufficientBalanceException;
import com.example.finpay.payment_service.exceptions.PaymentNotFoundException;
import com.example.finpay.payment_service.exceptions.SameAccountTransferException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountClient accountClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final PaymentEventPublisher paymentEventPublisher;

    public PaymentResponse processPayment(PaymentRequest request) {

        String redisKey = "payment:idem:" + request.keyIdempotence();
        String cached = (String) redisTemplate.opsForValue().get(redisKey);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, PaymentResponse.class);
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                throw new RuntimeException("Error deserializing cached payment", e);
            }
        }

        AccountResponse accountOrigin = accountClient
                .findById(request.userOriginatingId(), request.originatingAccount());

        AccountResponse accountDestination = accountClient
                .findById(request.userDestinationId(), request.destinationAccount());

        if (Objects.equals(accountOrigin.id(), accountDestination.id())) {
            throw new SameAccountTransferException("The accounts are the same!");
        }

        if (accountOrigin.status() != AccountStatus.ACTIVE
                || accountDestination.status() != AccountStatus.ACTIVE) {
            throw new AccountBlockedException("The account is not active!");
        }

        if (accountOrigin.balance().compareTo(BigDecimal.ZERO) <= 0
                || request.value().compareTo(accountOrigin.balance()) > 0) {
            throw new InsufficientBalanceException(
                    "The account: " + accountOrigin.id() + " does not have sufficient balance!"
            );
        }

        accountClient.updateBalance(
                request.userOriginatingId(),
                request.originatingAccount(),
                new UpdateBalanceRequest(request.value(), BalanceOperation.DEBIT)
        );

        accountClient.updateBalance(
                request.userDestinationId(),
                request.destinationAccount(),
                new UpdateBalanceRequest(request.value(), BalanceOperation.CREDIT)
        );

        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .sourceAccountId(request.originatingAccount())
                .destinationAccountId(request.destinationAccount())
                .amount(request.value())
                .transactionStatus(TransactionStatus.COMPLETED)
                .idempotencyKey(request.keyIdempotence())
                .description(request.description())
                .createdAt(Instant.now())
                .processedAt(Instant.now())
                .build();

        Transaction saved = transactionRepository.save(transaction);

        paymentEventPublisher.publishPaymentCompleted(new PaymentCompletedEvent(
                saved.getId(),
                saved.getSourceAccountId(),
                saved.getDestinationAccountId(),
                saved.getAmount(),
                saved.getTransactionStatus(),
                saved.getProcessedAt()
        ));

        try {
            String json = objectMapper.writeValueAsString(PaymentResponse.from(saved));
            redisTemplate.opsForValue().set(redisKey, json, 24, TimeUnit.HOURS);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Error serializing payment response", e);
        }

        return PaymentResponse.from(saved);
    }

    public PaymentResponse findById(String id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment Not Found !"));

        return PaymentResponse.from(transaction);
    }

    public List<PaymentResponse> findBySourceAccountId(String accountId) {
        List<Transaction> transactions = transactionRepository.findBySourceAccountId(accountId);

        return transactions.stream().map(PaymentResponse::from).toList();
    }
}
