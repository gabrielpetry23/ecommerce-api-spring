package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductReviewDTO(

        @Min(1) @Max(10)
        @NotNull(message = "Rating is required")
        Integer rating,
        @NotBlank(message = "Comment is required")
        String comment
) {
}
