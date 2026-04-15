package com.example.finpay.account_service.controllers;


import com.example.finpay.account_service.dto.account.AccountRequest;
import com.example.finpay.account_service.dto.account.AccountResponse;
import com.example.finpay.account_service.enums.AccountStatus;
import com.example.finpay.account_service.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    

}
