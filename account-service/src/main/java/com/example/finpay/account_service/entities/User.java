package com.example.finpay.account_service.entities;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import com.example.finpay.account_service.enums.AccountStatus;
import com.example.finpay.account_service.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Container(containerName = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    @PartitionKey
    private String email;

    private String name;
    private String cpf;
    private UserStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
