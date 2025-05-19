package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import java.math.BigDecimal;
import java.util.List;

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