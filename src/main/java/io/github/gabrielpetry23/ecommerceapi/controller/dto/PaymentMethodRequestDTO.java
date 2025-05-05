package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record PaymentMethodRequestDTO(
        @NotBlank(message = "Type is required")
        String type,
        @NotBlank(message = "Card number is required")
        String cardNumber,
        @NotBlank(message = "Card holder name is required")
        String cardHolderName,
        @NotBlank(message = "Expiry date is required")
        String expiryDate,
        @NotBlank(message = "CVV is required")
        String cvv,
        @NotBlank(message = "Provider is required")
        String provider
) {
}
