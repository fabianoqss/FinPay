package com.example.finpay.account_service.controllers;


import com.example.finpay.account_service.dto.account.AccountRequest;
import com.example.finpay.account_service.dto.account.AccountResponse;
import com.example.finpay.account_service.dto.account.BalanceResponse;
import com.example.finpay.account_service.dto.account.UserWithAccountsResponse;
import com.example.finpay.account_service.services.AccountService;
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

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/users/{userId}/accounts")
@Tag(name = "Accounts", description = "Account management operations")
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Create a new account", description = "Creates a financial account linked to an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@PathVariable String userId, @RequestBody @Valid AccountRequest accountRequest){
        AccountResponse response = accountService.createAccount(userId, accountRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "List accounts by user", description = "Returns all accounts belonging to the given user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Accounts listed successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping
    public ResponseEntity<List<AccountResponse>> findAllByUserId(@PathVariable String userId) {
        List<AccountResponse> response = accountService.findAllByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get account by ID", description = "Returns a single account by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping(value = "/{accountId}")
    public ResponseEntity<AccountResponse> findById(@PathVariable String accountId) {
        AccountResponse response = accountService.findById(accountId);
        return ResponseEntity.ok(response);
    }

    @Operation(    summary = "Get user with accounts",
            description = "Returns a single user with all their accounts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/with-accounts")
    public ResponseEntity<UserWithAccountsResponse> findUserWithAccounts(
            @PathVariable @Parameter(description = "User ID", required = true)
            String userId) {
        UserWithAccountsResponse response = accountService.findUserWithAccounts(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get account balance", description = "Returns the current balance of the account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Balance retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping(value = "/{accountId}/balance")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable String accountId) {
        BalanceResponse response = accountService.getBalance(accountId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Block account", description = "Changes the account status to BLOCKED")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account blocked successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PatchMapping(value = "/{accountId}/block")
    public ResponseEntity<Void> blockAccount(@PathVariable String accountId) {
        accountService.blockAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activate account", description = "Changes the account status to ACTIVE")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account activated successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PatchMapping(value = "/{accountId}/activate")
    public ResponseEntity<Void> activateAccount(@PathVariable String accountId) {
        accountService.activateAccount(accountId);
        return ResponseEntity.noContent().build();
    }

}
