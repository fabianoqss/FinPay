package com.example.finpay.payment_service.config;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceBusConfig {

    @Bean
    public ServiceBusSenderClient serviceBusSenderClient(
            @Value("${spring.cloud.azure.servicebus.connection-string}") String connectionString) {
        return new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .topicName("payment-events")
                .buildClient();
    }
}
