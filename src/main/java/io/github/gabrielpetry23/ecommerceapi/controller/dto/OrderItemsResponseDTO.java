package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import java.math.BigDecimal;

public record OrderItemsResponseDTO(
        ProductResponseDTO product,
        Integer quantity,
        BigDecimal price
) {
}
