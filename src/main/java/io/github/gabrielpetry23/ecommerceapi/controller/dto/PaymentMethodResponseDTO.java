package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import java.time.LocalDate;
import java.util.UUID;

public record PaymentMethodResponseDTO(
        UUID id,
        String type,
        String provider,
        String last4Digits,
        String cardBrand
) {
}
