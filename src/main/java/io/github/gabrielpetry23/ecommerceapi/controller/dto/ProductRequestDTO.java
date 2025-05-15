package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import io.github.gabrielpetry23.ecommerceapi.model.ProductImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductRequestDTO(

        @NotBlank(message = "Name is required")
        String name,

//        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        BigDecimal price,

        @NotNull(message = "Stock is required")
        @Positive(message = "Stock must be positive")
        Integer stock,

        @NotNull(message = "Category ID is required")
        UUID categoryId
) {
}
