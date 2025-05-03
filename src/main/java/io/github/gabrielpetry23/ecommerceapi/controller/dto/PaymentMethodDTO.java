package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import java.time.LocalDate;

public record PaymentMethodDTO(
        String type,
        String cardNumber,
        String cardHolderName,
        String expiryDate,
        String cvv,
        String provider
) {
}
