package com.example.finpay.account_service.repositories;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.example.finpay.account_service.entities.Account;

public interface AccountRepository extends CosmosRepository<Account, String> {
}
