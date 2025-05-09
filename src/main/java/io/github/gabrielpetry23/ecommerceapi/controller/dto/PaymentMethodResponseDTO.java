package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record PaymentMethodResponseDTO(
        UserNameIdDTO user,
        String type,
        String cardNumber,
        String cardHolderName,
        LocalDate expiryDate,
        String cvv,
        String provider
) {
}
