package com.example.finpay.account_service.dto.account;

import com.example.finpay.account_service.enums.BalanceOperation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateBalanceRequest(
        @NotNull @Positive BigDecimal amount,
        @NotNull BalanceOperation operation
) {}
