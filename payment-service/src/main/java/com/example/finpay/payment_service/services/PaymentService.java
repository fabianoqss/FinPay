package com.example.finpay.payment_service.services;

import com.example.finpay.payment_service.client.AccountClient;
import com.example.finpay.payment_service.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private AccountClient accountClient;







}
