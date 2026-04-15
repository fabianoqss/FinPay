package com.example.finpay.account_service.dto.account;

import com.example.finpay.account_service.entities.Account;
import com.example.finpay.account_service.enums.AccountStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(
        String id,
        String userId,
        BigDecimal balance,
        AccountStatus status,
        Instant createdAt
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getUserId(),
                account.getBalance(),
                account.getStatus(),
                account.getCreatedAt()
        );
    }

}
