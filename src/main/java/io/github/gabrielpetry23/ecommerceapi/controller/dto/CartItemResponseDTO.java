package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponseDTO(
        UUID id,
        ProductIdentifierDTO product,
        Integer quantity,
        BigDecimal totalPrice
) {
}
