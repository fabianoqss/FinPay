package com.example.finpay.payment_service.controllers;

import com.example.finpay.payment_service.dto.PaymentRequest;
import com.example.finpay.payment_service.dto.PaymentResponse;
import com.example.finpay.payment_service.enums.TransactionStatus;
import com.example.finpay.payment_service.services.TransactionService;
import com.example.finpay.payment_service.services.exceptions.PaymentNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private TransactionService transactionService;
    // prevents Spring Security from trying to connect to the JWT issuer URI
    @MockBean private JwtDecoder jwtDecoder;

    private static final String ORIGIN_ACCOUNT_ID = "account-origin-1";
    private static final String DEST_ACCOUNT_ID = "account-dest-1";

    private PaymentRequest validRequest() {
        return new PaymentRequest(
                UUID.randomUUID().toString(),
                "user-origin-1", ORIGIN_ACCOUNT_ID,
                "user-dest-1", DEST_ACCOUNT_ID,
                BigDecimal.valueOf(100),
                "Test transfer"
        );
    }

    private PaymentResponse sampleResponse() {
        return new PaymentResponse(
                "tx-1", ORIGIN_ACCOUNT_ID, DEST_ACCOUNT_ID,
                BigDecimal.valueOf(100), TransactionStatus.COMPLETED,
                Instant.now(), Instant.now()
        );
    }

    // ─── POST /api/payments ───────────────────────────────────────────────────

    @Test
    void processPayment_withValidRequest_returns201WithBody() throws Exception {
        when(transactionService.processPayment(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/payments")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("tx-1"))
                .andExpect(jsonPath("$.transactionStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.sourceAccountId").value(ORIGIN_ACCOUNT_ID));
    }

    @Test
    void processPayment_withMissingKeyIdempotence_returns400WithFieldError() throws Exception {
        PaymentRequest request = new PaymentRequest(
                null,
                "user-origin-1", ORIGIN_ACCOUNT_ID,
                "user-dest-1", DEST_ACCOUNT_ID,
                BigDecimal.valueOf(100), "Test"
        );

        mockMvc.perform(post("/api/payments")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.keyIdempotence").exists());
    }

    @Test
    void processPayment_withInvalidUuidFormat_returns400WithMessage() throws Exception {
        PaymentRequest request = new PaymentRequest(
                "not-a-uuid",
                "user-origin-1", ORIGIN_ACCOUNT_ID,
                "user-dest-1", DEST_ACCOUNT_ID,
                BigDecimal.valueOf(100), "Test"
        );

        mockMvc.perform(post("/api/payments")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.keyIdempotence").value("Idempotency key must be a valid UUID"));
    }

    @Test
    void processPayment_withNegativeValue_returns400WithMessage() throws Exception {
        PaymentRequest request = new PaymentRequest(
                UUID.randomUUID().toString(),
                "user-origin-1", ORIGIN_ACCOUNT_ID,
                "user-dest-1", DEST_ACCOUNT_ID,
                BigDecimal.valueOf(-10), "Test"
        );

        mockMvc.perform(post("/api/payments")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.value").value("Value must be greater than zero"));
    }

    @Test
    void processPayment_withDescriptionExceeding255Chars_returns400WithFieldError() throws Exception {
        PaymentRequest request = new PaymentRequest(
                UUID.randomUUID().toString(),
                "user-origin-1", ORIGIN_ACCOUNT_ID,
                "user-dest-1", DEST_ACCOUNT_ID,
                BigDecimal.valueOf(100), "A".repeat(256)
        );

        mockMvc.perform(post("/api/payments")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.description").exists());
    }

    @Test
    void processPayment_withMissingOriginAccountId_returns400WithFieldError() throws Exception {
        PaymentRequest request = new PaymentRequest(
                UUID.randomUUID().toString(),
                "user-origin-1", null,
                "user-dest-1", DEST_ACCOUNT_ID,
                BigDecimal.valueOf(100), "Test"
        );

        mockMvc.perform(post("/api/payments")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.originatingAccount").exists());
    }

    // ─── GET /api/payments/{id} ───────────────────────────────────────────────

    @Test
    void findById_whenTransactionExists_returns200WithBody() throws Exception {
        when(transactionService.findById("tx-1")).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/payments/tx-1").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("tx-1"))
                .andExpect(jsonPath("$.transactionStatus").value("COMPLETED"));
    }

    @Test
    void findById_whenTransactionNotFound_returns404WithMessage() throws Exception {
        when(transactionService.findById("missing")).thenThrow(new PaymentNotFoundException("Payment Not Found !"));

        mockMvc.perform(get("/api/payments/missing").with(jwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Payment Not Found !"))
                .andExpect(jsonPath("$.status").value(404));
    }

    // ─── GET /api/payments/account/{accountId} ────────────────────────────────

    @Test
    void findBySourceAccountId_whenTransactionsExist_returns200WithList() throws Exception {
        when(transactionService.findBySourceAccountId(ORIGIN_ACCOUNT_ID))
                .thenReturn(List.of(sampleResponse(), sampleResponse()));

        mockMvc.perform(get("/api/payments/account/" + ORIGIN_ACCOUNT_ID).with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void findBySourceAccountId_whenNoTransactions_returns200WithEmptyList() throws Exception {
        when(transactionService.findBySourceAccountId("unknown")).thenReturn(List.of());

        mockMvc.perform(get("/api/payments/account/unknown").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
