package com.example.finpay.account_service.dto.auth;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Long expiresIn,
        String userId,
        String email
) {
}
