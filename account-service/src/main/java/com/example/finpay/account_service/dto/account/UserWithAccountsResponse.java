package com.example.finpay.account_service.dto.account;

import com.example.finpay.account_service.dto.user.UserResponse;

import java.util.List;

public record UserWithAccountsResponse(
        UserResponse user,
        List<AccountResponse> accounts
) {}
