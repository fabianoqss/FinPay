package com.example.finpay.payment_service.repositories;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.example.finpay.payment_service.entities.Transaction;

public interface PaymentRepository extends CosmosRepository<Transaction, String> {
}
