package com.example.finpay.payment_service.services;

import com.example.finpay.payment_service.client.AccountClient;
import com.example.finpay.payment_service.client.dto.payment.PaymentRequest;
import com.example.finpay.payment_service.client.dto.payment.PaymentResponse;
import com.example.finpay.payment_service.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository paymentRepository;
    private AccountClient accountClient;
    private final RedisTemplate<String, Object> redisTemplate;

    public PaymentResponse processPayment(PaymentRequest request){
        

        return null;
    }






}
