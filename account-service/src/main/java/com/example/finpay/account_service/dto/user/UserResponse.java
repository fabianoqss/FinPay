package com.example.finpay.account_service.dto.user;

import com.example.finpay.account_service.entities.User;
import com.example.finpay.account_service.enums.UserStatus;

import java.time.Instant;

public record UserResponse(
        String id,
        String email,
        String name,
         String cpf,
         UserStatus status,
         Instant createdAt,
         Instant updatedAt) {


    public static UserResponse from(User user){
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getCpf(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
                );
    }
}
