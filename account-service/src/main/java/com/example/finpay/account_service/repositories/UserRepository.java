package com.example.finpay.account_service.repositories;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.example.finpay.account_service.entities.User;

import java.util.Optional;

public interface UserRepository extends CosmosRepository<User, String> {
    Optional<User> findByEmail(String email);

}
