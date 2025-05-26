package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PaymentMethodRequestDTO(
        @NotBlank(message = "Type is required")
        String type,
        @NotBlank(message = "Provider is required")
        String provider,
        @NotBlank(message = "Payment token is required")
        @Size(min = 10, max = 255, message = "Token size invalid")
        String paymentToken,
        @Size(min = 4, max = 4, message = "Last 4 digits must be 4 characters")
        String last4Digits,
        String cardBrand
) {
}
