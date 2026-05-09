package com.example.finpay.payment_service.controllers;

import com.example.finpay.payment_service.dto.PaymentRequest;
import com.example.finpay.payment_service.dto.PaymentResponse;
import com.example.finpay.payment_service.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Processamento e consulta de transações financeiras")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Processar pagamento", description = "Realiza uma transferência entre duas contas. Idempotente via keyIdempotence.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pagamento processado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "402", description = "Saldo insuficiente"),
            @ApiResponse(responseCode = "409", description = "Pagamento duplicado ou conta bloqueada"),
            @ApiResponse(responseCode = "422", description = "Transferência entre a mesma conta")
    })
    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = transactionService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Buscar transação por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transação encontrada"),
            @ApiResponse(responseCode = "404", description = "Transação não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> findById(
            @Parameter(description = "ID da transação") @PathVariable String id) {
        return ResponseEntity.ok(transactionService.findById(id));
    }

    @Operation(summary = "Listar transações por conta de origem")
    @ApiResponse(responseCode = "200", description = "Lista de transações da conta")
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<PaymentResponse>> findBySourceAccountId(
            @Parameter(description = "ID da conta de origem") @PathVariable String accountId) {
        return ResponseEntity.ok(transactionService.findBySourceAccountId(accountId));
    }
}
