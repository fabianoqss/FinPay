package com.example.finpay.account_service.services.exceptions;

public class AccountNotFoundExcepetion extends RuntimeException {
    public AccountNotFoundExcepetion(String id) {
        super("Account not found , id : " + id);
    }
}
