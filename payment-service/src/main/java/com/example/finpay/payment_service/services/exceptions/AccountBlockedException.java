package com.example.finpay.payment_service.services.exceptions;

public class AccountBlockedException extends RuntimeException {
    public AccountBlockedException(String message) {
        super(message);
    }
}
