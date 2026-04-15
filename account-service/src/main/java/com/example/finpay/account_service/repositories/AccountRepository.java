package com.example.finpay.account_service.repositories;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.example.finpay.account_service.entities.Account;

import java.util.List;

public interface AccountRepository extends CosmosRepository<Account, String> {
    List<Account> findByUserId(String userId);
}
