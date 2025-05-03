package io.github.gabrielpetry23.ecommerceapi.controller.dto;

public record ProductReviewResponseDTO(
        UserNameIdDTO user,
        Integer rating,
        String comment
) {
}
