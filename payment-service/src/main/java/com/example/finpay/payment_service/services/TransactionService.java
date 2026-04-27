package com.example.finpay.payment_service.services;

import com.example.finpay.payment_service.client.AccountClient;
import com.example.finpay.payment_service.client.dto.AccountResponse;
import com.example.finpay.payment_service.client.dto.AccountStatus;
import com.example.finpay.payment_service.client.dto.payment.PaymentRequest;
import com.example.finpay.payment_service.client.dto.payment.PaymentResponse;
import com.example.finpay.payment_service.entities.Transaction;
import com.example.finpay.payment_service.repositories.TransactionRepository;
import com.example.finpay.payment_service.services.exceptions.AccountBlockedException;
import com.example.finpay.payment_service.services.exceptions.SameAccountTransferException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private AccountClient accountClient;
    private final RedisTemplate<String, Object> redisTemplate;

    public PaymentResponse processPayment(PaymentRequest request){

    AccountResponse accountOrigin = accountClient.findById(request.id(), request.originatingAccount());

    AccountResponse accountDestin = accountClient.findById(request.id(), request.originatingAccount());

    if(Objects.equals(accountOrigin.id(), accountDestin.id())){
        throw new SameAccountTransferException("The accounts are the same!");
    }

    if(accountOrigin.status() != AccountStatus.ACTIVE || accountDestin.status() == AccountStatus.ACTIVE){
        throw new AccountBlockedException(" The Account is not Active! ");
    }




        return null;
    }






}
