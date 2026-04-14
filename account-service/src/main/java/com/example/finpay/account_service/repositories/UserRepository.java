package com.example.finpay.account_service.repositories;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.example.finpay.account_service.entities.User;

public interface UserRepository extends CosmosRepository<User, String> {
}
