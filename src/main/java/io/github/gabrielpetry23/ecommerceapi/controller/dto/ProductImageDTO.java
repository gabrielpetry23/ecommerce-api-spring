package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record ProductImageDTO(
        @NotBlank(message = "Image URL cannot be blank")
        String imageUrl,
        boolean isMain
) {
}
