package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CartItemRequestDTO(
        @NotNull(message = "Product ID is required")
        String productId,
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
        //
) {
}
