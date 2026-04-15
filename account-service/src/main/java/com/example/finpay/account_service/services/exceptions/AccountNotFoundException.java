package com.example.finpay.account_service.services.exceptions;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String id) {
        super("Account not found , id : " + id);
    }
}
