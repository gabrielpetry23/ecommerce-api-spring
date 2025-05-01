package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemDTO(
//        UUID id,
//        UUID productId,
        @NotNull(message = "Product ID is required")
        UUID productId,
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,
        BigDecimal price
) {
}
