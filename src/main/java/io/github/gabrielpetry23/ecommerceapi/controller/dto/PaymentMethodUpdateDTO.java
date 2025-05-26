package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PaymentMethodUpdateDTO(
        String type,
        String provider,
        String cardBrand
) {
}
