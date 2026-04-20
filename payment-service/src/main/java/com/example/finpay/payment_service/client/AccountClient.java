package com.example.finpay.payment_service.client;

import com.example.finpay.payment_service.client.exceptions.FeignConfig;
import com.example.finpay.payment_service.dto.AccountResponse;
import com.example.finpay.payment_service.dto.UpdateBalanceRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "account-service", url = "${account.service.url}",
        configuration = FeignConfig.class
)
public interface AccountClient {

    @GetMapping("/api/users/{userId}/accounts/{accountId}")
    AccountResponse findById(
            @PathVariable String userId,
            @PathVariable String accountId
    );

    @PatchMapping("/api/users/{userId}/accounts/{accountId}/balance")
    void updateBalance(
            @PathVariable String userId,
            @PathVariable String accountId,
            @RequestBody UpdateBalanceRequest request
    );
}
