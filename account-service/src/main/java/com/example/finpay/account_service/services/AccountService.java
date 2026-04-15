package com.example.finpay.account_service.services;


import com.example.finpay.account_service.dto.account.AccountRequest;
import com.example.finpay.account_service.dto.account.AccountResponse;
import com.example.finpay.account_service.entities.Account;
import com.example.finpay.account_service.enums.AccountStatus;
import com.example.finpay.account_service.repositories.AccountRepository;
import com.example.finpay.account_service.repositories.UserRepository;
import com.example.finpay.account_service.services.exceptions.AccountNotFoundExcepetion;
import com.example.finpay.account_service.services.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;


    public AccountResponse createAccount(String userId, AccountRequest request){
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Account account = Account.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .balance(request.initialBalance())
                .status(AccountStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        accountRepository.save(account);
        Account savedAccount = accountRepository.save(account);
        return AccountResponse.from(savedAccount);
    }

    public AccountResponse findById(String accountId){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundExcepetion(accountId));

        return AccountResponse.from(account);
    }

    public List<AccountResponse> findAllByUserId(String userId){
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return accountRepository.findByUserId(userId)
                .stream()
                .map(AccountResponse::from)
                .toList();
    }

}
