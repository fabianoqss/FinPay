package com.example.finpay.payment_service.services;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.example.finpay.payment_service.events.PaymentCompletedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final ServiceBusSenderClient senderClient;
    private final ObjectMapper objectMapper;

    public void publishPaymentCompleted(PaymentCompletedEvent event) {
        try {
            String body = objectMapper.writeValueAsString(event);
            ServiceBusMessage message = new ServiceBusMessage(body)
                    .setContentType("application/json")
                    .setMessageId(event.transactionId());
            senderClient.sendMessage(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing PaymentCompletedEvent", e);
        }
    }
}
