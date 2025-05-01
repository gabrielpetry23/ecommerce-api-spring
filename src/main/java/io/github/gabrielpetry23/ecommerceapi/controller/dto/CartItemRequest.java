package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import java.util.UUID;

public record CartItemRequest(
        UUID productId,
        Integer quantity
) {
}
