package io.github.gabrielpetry23.ecommerceapi.controller.dto;


import java.math.BigDecimal;

public record OrderItemsResponseDTO(
        ProductIdentifierDTO product,
        Integer quantity,
        BigDecimal itemPrice
) {
}
