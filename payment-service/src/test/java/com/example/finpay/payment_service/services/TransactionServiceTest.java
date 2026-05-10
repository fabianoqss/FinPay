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
import com.example.finpay.payment_service.repositories.TransactionRepository;
import com.example.finpay.payment_service.exceptions.AccountBlockedException;
import com.example.finpay.payment_service.exceptions.InsufficientBalanceException;
import com.example.finpay.payment_service.exceptions.PaymentNotFoundException;
import com.example.finpay.payment_service.exceptions.SameAccountTransferException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private AccountClient accountClient;
    @Mock private RedisTemplate<String, Object> redisTemplate;
    @Mock private ObjectMapper objectMapper;
    @Mock private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private TransactionService transactionService;

    private static final String IDEM_KEY = UUID.randomUUID().toString();
    private static final String ORIGIN_USER_ID = "user-origin-1";
    private static final String ORIGIN_ACCOUNT_ID = "account-origin-1";
    private static final String DEST_USER_ID = "user-dest-1";
    private static final String DEST_ACCOUNT_ID = "account-dest-1";
    private static final String REDIS_KEY = "payment:idem:" + IDEM_KEY;

    private PaymentRequest validRequest;
    private AccountResponse activeOriginAccount;
    private AccountResponse activeDestAccount;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        validRequest = new PaymentRequest(
                IDEM_KEY,
                ORIGIN_USER_ID, ORIGIN_ACCOUNT_ID,
                DEST_USER_ID, DEST_ACCOUNT_ID,
                BigDecimal.valueOf(100),
                "Test transfer"
        );

        activeOriginAccount = new AccountResponse(
                ORIGIN_ACCOUNT_ID, ORIGIN_USER_ID, BigDecimal.valueOf(500), AccountStatus.ACTIVE, Instant.now()
        );

        activeDestAccount = new AccountResponse(
                DEST_ACCOUNT_ID, DEST_USER_ID, BigDecimal.valueOf(200), AccountStatus.ACTIVE, Instant.now()
        );
    }

    // ─── processPayment ───────────────────────────────────────────────────────

    @Test
    void processPayment_whenCacheHit_returnsCachedResponseWithoutCallingDownstream() throws Exception {
        String cachedJson = "{\"id\":\"tx-cached\"}";
        PaymentResponse cachedResponse = new PaymentResponse(
                "tx-cached", ORIGIN_ACCOUNT_ID, DEST_ACCOUNT_ID,
                BigDecimal.valueOf(100), TransactionStatus.COMPLETED,
                Instant.now(), Instant.now()
        );

        when(valueOperations.get(REDIS_KEY)).thenReturn(cachedJson);
        when(objectMapper.readValue(cachedJson, PaymentResponse.class)).thenReturn(cachedResponse);

        PaymentResponse result = transactionService.processPayment(validRequest);

        assertThat(result).isEqualTo(cachedResponse);
        verifyNoInteractions(accountClient);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void processPayment_whenSameAccount_throwsSameAccountTransferException() {
        when(valueOperations.get(REDIS_KEY)).thenReturn(null);
        when(accountClient.findById(ORIGIN_USER_ID, ORIGIN_ACCOUNT_ID)).thenReturn(activeOriginAccount);
        when(accountClient.findById(DEST_USER_ID, DEST_ACCOUNT_ID)).thenReturn(activeOriginAccount);

        assertThatThrownBy(() -> transactionService.processPayment(validRequest))
                .isInstanceOf(SameAccountTransferException.class)
                .hasMessageContaining("same");
    }

    @Test
    void processPayment_whenOriginAccountBlocked_throwsAccountBlockedException() {
        AccountResponse blocked = new AccountResponse(
                ORIGIN_ACCOUNT_ID, ORIGIN_USER_ID, BigDecimal.valueOf(500), AccountStatus.BLOCKED, Instant.now()
        );

        when(valueOperations.get(REDIS_KEY)).thenReturn(null);
        when(accountClient.findById(ORIGIN_USER_ID, ORIGIN_ACCOUNT_ID)).thenReturn(blocked);
        when(accountClient.findById(DEST_USER_ID, DEST_ACCOUNT_ID)).thenReturn(activeDestAccount);

        assertThatThrownBy(() -> transactionService.processPayment(validRequest))
                .isInstanceOf(AccountBlockedException.class);
    }

    @Test
    void processPayment_whenDestinationAccountBlocked_throwsAccountBlockedException() {
        AccountResponse blocked = new AccountResponse(
                DEST_ACCOUNT_ID, DEST_USER_ID, BigDecimal.valueOf(200), AccountStatus.BLOCKED, Instant.now()
        );

        when(valueOperations.get(REDIS_KEY)).thenReturn(null);
        when(accountClient.findById(ORIGIN_USER_ID, ORIGIN_ACCOUNT_ID)).thenReturn(activeOriginAccount);
        when(accountClient.findById(DEST_USER_ID, DEST_ACCOUNT_ID)).thenReturn(blocked);

        assertThatThrownBy(() -> transactionService.processPayment(validRequest))
                .isInstanceOf(AccountBlockedException.class);
    }

    @Test
    void processPayment_whenOriginBalanceIsZero_throwsInsufficientBalanceException() {
        AccountResponse zeroBalance = new AccountResponse(
                ORIGIN_ACCOUNT_ID, ORIGIN_USER_ID, BigDecimal.ZERO, AccountStatus.ACTIVE, Instant.now()
        );

        when(valueOperations.get(REDIS_KEY)).thenReturn(null);
        when(accountClient.findById(ORIGIN_USER_ID, ORIGIN_ACCOUNT_ID)).thenReturn(zeroBalance);
        when(accountClient.findById(DEST_USER_ID, DEST_ACCOUNT_ID)).thenReturn(activeDestAccount);

        assertThatThrownBy(() -> transactionService.processPayment(validRequest))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining(ORIGIN_ACCOUNT_ID);
    }

    @Test
    void processPayment_whenRequestValueExceedsBalance_throwsInsufficientBalanceException() {
        // balance = 10, request value = 100
        AccountResponse lowBalance = new AccountResponse(
                ORIGIN_ACCOUNT_ID, ORIGIN_USER_ID, BigDecimal.valueOf(10), AccountStatus.ACTIVE, Instant.now()
        );

        when(valueOperations.get(REDIS_KEY)).thenReturn(null);
        when(accountClient.findById(ORIGIN_USER_ID, ORIGIN_ACCOUNT_ID)).thenReturn(lowBalance);
        when(accountClient.findById(DEST_USER_ID, DEST_ACCOUNT_ID)).thenReturn(activeDestAccount);

        assertThatThrownBy(() -> transactionService.processPayment(validRequest))
                .isInstanceOf(InsufficientBalanceException.class);
    }

    @Test
    void processPayment_whenValid_debitsCreditsSavesAndCachesResponse() throws Exception {
        Transaction saved = Transaction.builder()
                .id("tx-123")
                .sourceAccountId(ORIGIN_ACCOUNT_ID)
                .destinationAccountId(DEST_ACCOUNT_ID)
                .amount(BigDecimal.valueOf(100))
                .transactionStatus(TransactionStatus.COMPLETED)
                .idempotencyKey(IDEM_KEY)
                .description("Test transfer")
                .createdAt(Instant.now())
                .processedAt(Instant.now())
                .build();

        when(valueOperations.get(REDIS_KEY)).thenReturn(null);
        when(accountClient.findById(ORIGIN_USER_ID, ORIGIN_ACCOUNT_ID)).thenReturn(activeOriginAccount);
        when(accountClient.findById(DEST_USER_ID, DEST_ACCOUNT_ID)).thenReturn(activeDestAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        PaymentResponse result = transactionService.processPayment(validRequest);

        assertThat(result.id()).isEqualTo("tx-123");
        assertThat(result.sourceAccountId()).isEqualTo(ORIGIN_ACCOUNT_ID);
        assertThat(result.destinationAccountId()).isEqualTo(DEST_ACCOUNT_ID);
        assertThat(result.transactionStatus()).isEqualTo(TransactionStatus.COMPLETED);

        verify(accountClient).updateBalance(
                eq(ORIGIN_USER_ID), eq(ORIGIN_ACCOUNT_ID),
                eq(new UpdateBalanceRequest(BigDecimal.valueOf(100), BalanceOperation.DEBIT))
        );
        verify(accountClient).updateBalance(
                eq(DEST_USER_ID), eq(DEST_ACCOUNT_ID),
                eq(new UpdateBalanceRequest(BigDecimal.valueOf(100), BalanceOperation.CREDIT))
        );
        verify(transactionRepository).save(any(Transaction.class));
        verify(valueOperations).set(eq(REDIS_KEY), any(), eq(24L), eq(TimeUnit.HOURS));
    }

    // ─── findById ────────────────────────────────────────────────────────────

    @Test
    void findById_whenTransactionExists_returnsPaymentResponse() {
        Transaction transaction = Transaction.builder()
                .id("tx-1")
                .sourceAccountId(ORIGIN_ACCOUNT_ID)
                .destinationAccountId(DEST_ACCOUNT_ID)
                .amount(BigDecimal.valueOf(100))
                .transactionStatus(TransactionStatus.COMPLETED)
                .createdAt(Instant.now())
                .processedAt(Instant.now())
                .build();

        when(transactionRepository.findById("tx-1")).thenReturn(Optional.of(transaction));

        PaymentResponse result = transactionService.findById("tx-1");

        assertThat(result.id()).isEqualTo("tx-1");
        assertThat(result.transactionStatus()).isEqualTo(TransactionStatus.COMPLETED);
    }

    @Test
    void findById_whenTransactionNotFound_throwsPaymentNotFoundException() {
        when(transactionRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.findById("missing"))
                .isInstanceOf(PaymentNotFoundException.class)
                .hasMessageContaining("Payment Not Found");
    }

    // ─── findBySourceAccountId ───────────────────────────────────────────────

    @Test
    void findBySourceAccountId_whenTransactionsExist_returnsMappedList() {
        Transaction t1 = Transaction.builder()
                .id("tx-1").sourceAccountId(ORIGIN_ACCOUNT_ID).destinationAccountId(DEST_ACCOUNT_ID)
                .amount(BigDecimal.valueOf(50)).transactionStatus(TransactionStatus.COMPLETED)
                .createdAt(Instant.now()).processedAt(Instant.now()).build();

        Transaction t2 = Transaction.builder()
                .id("tx-2").sourceAccountId(ORIGIN_ACCOUNT_ID).destinationAccountId(DEST_ACCOUNT_ID)
                .amount(BigDecimal.valueOf(75)).transactionStatus(TransactionStatus.COMPLETED)
                .createdAt(Instant.now()).processedAt(Instant.now()).build();

        when(transactionRepository.findBySourceAccountId(ORIGIN_ACCOUNT_ID)).thenReturn(List.of(t1, t2));

        List<PaymentResponse> result = transactionService.findBySourceAccountId(ORIGIN_ACCOUNT_ID);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(PaymentResponse::id).containsExactly("tx-1", "tx-2");
    }

    @Test
    void findBySourceAccountId_whenNoTransactionsFound_returnsEmptyList() {
        when(transactionRepository.findBySourceAccountId(ORIGIN_ACCOUNT_ID)).thenReturn(List.of());

        List<PaymentResponse> result = transactionService.findBySourceAccountId(ORIGIN_ACCOUNT_ID);

        assertThat(result).isEmpty();
    }
}
