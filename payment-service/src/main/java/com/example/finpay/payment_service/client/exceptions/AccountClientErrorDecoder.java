package com.example.finpay.payment_service.client.exceptions;

import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;

import javax.security.auth.login.AccountNotFoundException;

public class AccountClientErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 404 -> new AccountNotFoundException(
                    "Account not found"
            );
            case 409 -> new AccountBlockedException(
                    "Account is blocked"
            );
            case 402 -> new InsufficientBalanceException(
                    "Insufficient balance"
            );
            default -> new FeignException.FeignClientException(
                    response.status(),
                    "Unexpected error calling Account Service",
                    response.request(),
                    null,
                    null
            );
        };
    }
}
