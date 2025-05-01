package io.github.gabrielpetry23.ecommerceapi.controller.dto;

public record ProductReviewRequestDTO(
        Integer rating,
        String comment
) {
}
