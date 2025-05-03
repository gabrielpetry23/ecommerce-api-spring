package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import java.util.UUID;

public record UserNameIdDTO(
        UUID id,
        String name
) {
}
