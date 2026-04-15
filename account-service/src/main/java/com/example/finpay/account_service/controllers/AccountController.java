package com.example.finpay.account_service.controllers;


import com.example.finpay.account_service.dto.account.AccountRequest;
import com.example.finpay.account_service.dto.account.AccountResponse;
import com.example.finpay.account_service.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(String userId, AccountRequest accountRequest){
        AccountResponse response = accountService.createAccount(userId, accountRequest);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AccountResponse> findById(@PathVariable String id) {
        AccountResponse response = accountService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<List<AccountResponse>> findAllByUserId(@PathVariable String userId) {
        List<AccountResponse> response = accountService.findAllByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "/{id}/block")
    public ResponseEntity<Void> blockAccount(@PathVariable String id) {
        accountService.blockAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}/activate")
    public ResponseEntity<Void> activateAccount(@PathVariable String id) {
        accountService.activateAccount(id);
        return ResponseEntity.noContent().build();
    }

}
