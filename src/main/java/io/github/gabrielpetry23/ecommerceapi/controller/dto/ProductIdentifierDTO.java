package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductIdentifierDTO(
        UUID id,
        String name,
        BigDecimal price
) {
}
