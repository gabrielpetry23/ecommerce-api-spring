package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import io.github.gabrielpetry23.ecommerceapi.model.ProductImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductResponseDTO(
        String name,
        String description,
        BigDecimal price,
        CategoryDTO category,
        List<ProductImageDTO> images,
        List<ProductReviewResponseDTO> reviews,
        Integer stock
) {
}