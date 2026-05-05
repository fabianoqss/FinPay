package com.example.finpay.payment_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PaymentRequest(

        @NotBlank(message = "Idempotency key is required")
        @Pattern(
                regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                message = "Idempotency key must be a valid UUID"
        )
        String keyIdempotence,

        @NotBlank(message = "Originating user ID is required")
        String userOriginatingId,

        @NotBlank(message = "Originating account ID is required")
        String originatingAccount,

        @NotBlank(message = "Destination user ID is required")
        String userDestinationId,

        @NotBlank(message = "Destination account ID is required")
        String destinationAccount,

        @NotNull(message = "Value is required")
        @Positive(message = "Value must be greater than zero")
        BigDecimal value,

        @Size(max = 255, message = "Description must not exceed 255 characters")
        String description

) {
}
