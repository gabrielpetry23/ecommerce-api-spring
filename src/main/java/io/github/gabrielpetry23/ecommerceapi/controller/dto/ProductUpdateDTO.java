package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import io.github.gabrielpetry23.ecommerceapi.model.ProductImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;
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
