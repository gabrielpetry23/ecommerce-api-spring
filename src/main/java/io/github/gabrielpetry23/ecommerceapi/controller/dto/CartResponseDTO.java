package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CartResponseDTO(
    UserNameIdDTO user,
    List<CartItemResponseDTO> items
) {
}
