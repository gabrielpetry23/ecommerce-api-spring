package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductUpdateDTO(

        String name,
        String description,
        @Positive(message = "Price must be positive")
        BigDecimal price,
        @Positive(message = "Stock must be positive")
        Integer stock,
        UUID categoryId
) {
}
