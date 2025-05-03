package io.github.gabrielpetry23.ecommerceapi.controller.dto;

public record ProductReviewResponseDTO(
        String userName,
        Integer rating,
        String comment
) {
}
