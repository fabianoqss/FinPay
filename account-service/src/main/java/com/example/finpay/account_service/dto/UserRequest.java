package com.example.finpay.account_service.dto;

public record UserRequest(
        String email,
        String name,
        String cpf
) {
}
