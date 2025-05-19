package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CouponDTO(
        String code,
        BigDecimal discountAmount,
        BigDecimal discountPercentage,
        String validUntil,
        Boolean isActive
) {
}
