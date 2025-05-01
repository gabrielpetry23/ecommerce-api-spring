package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import io.github.gabrielpetry23.ecommerceapi.model.Category;
import io.github.gabrielpetry23.ecommerceapi.model.ProductImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record ProductList(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        BigDecimal price,

        @NotBlank(message = "Category is required")
        Category category
) {
}
