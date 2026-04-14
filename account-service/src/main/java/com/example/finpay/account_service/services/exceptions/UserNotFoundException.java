package com.example.finpay.account_service.services.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String id) {
        super("User not found with id: " + id);
    }
}
