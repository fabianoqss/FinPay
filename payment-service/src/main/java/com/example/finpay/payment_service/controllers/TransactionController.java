package com.example.finpay.payment_service.controllers;

import com.example.finpay.payment_service.dto.PaymentRequest;
import com.example.finpay.payment_service.dto.PaymentResponse;
import com.example.finpay.payment_service.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = transactionService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(transactionService.findById(id));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<PaymentResponse>> findBySourceAccountId(@PathVariable String accountId) {
        return ResponseEntity.ok(transactionService.findBySourceAccountId(accountId));
    }
}
