package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record ApplyCouponRequestDTO(
        @NotBlank(message = "Coupon code is required")
        String couponCode
) {
}