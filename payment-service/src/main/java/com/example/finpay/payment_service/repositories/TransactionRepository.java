package com.example.finpay.payment_service.repositories;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.example.finpay.payment_service.entities.Transaction;

import java.util.List;

public interface TransactionRepository extends CosmosRepository<Transaction, String> {
    List<Transaction> findBySourceAccountId(String idAccount);
}
