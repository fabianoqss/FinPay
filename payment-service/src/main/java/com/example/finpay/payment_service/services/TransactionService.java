package com.example.finpay.payment_service.services;

import com.example.finpay.payment_service.client.AccountClient;
import com.example.finpay.payment_service.client.dto.AccountResponse;
import com.example.finpay.payment_service.client.dto.AccountStatus;
import com.example.finpay.payment_service.client.dto.payment.PaymentRequest;
import com.example.finpay.payment_service.client.dto.payment.PaymentResponse;
import com.example.finpay.payment_service.entities.Transaction;
import com.example.finpay.payment_service.repositories.TransactionRepository;
import com.example.finpay.payment_service.services.exceptions.AccountBlockedException;
import com.example.finpay.payment_service.services.exceptions.InsufficientBalanceException;
import com.example.finpay.payment_service.services.exceptions.PaymentNotFoundException;
import com.example.finpay.payment_service.services.exceptions.SameAccountTransferException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountClient accountClient;
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

    if(request.value().compareTo(accountOrigin.balance()) > 0 || accountOrigin.balance().compareTo(BigDecimal.ZERO) <= 0){
        throw new InsufficientBalanceException("The account : " + accountOrigin.id() + " Not have Balance , or the Value is more bigger than balance !" );
    }


    Transaction transaction = new Transaction();

        return null;
    }



    public PaymentResponse findById(String id){
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(()-> new PaymentNotFoundException("Payment Not Found ! "));

        return PaymentResponse.from(transaction);
    }

    public List<PaymentResponse> findBySourceAccountId(String accountId){
        List<Transaction> transactions = transactionRepository.findBySourceAccountId(accountId);

        return transactions.stream().map((transaction) -> PaymentResponse.from(transaction)).toList();
    }

}
