package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import java.util.List;

public record CartResponseDTO(
    UserNameIdDTO user,
    List<CartItemResponseDTO> items
) {
}
