package com.example.finpay.payment_service.client.account;

import com.example.finpay.payment_service.enums.BalanceOperation;

import java.math.BigDecimal;

public record UpdateBalanceRequest(
        BigDecimal amount,
        BalanceOperation operation
) {
}
