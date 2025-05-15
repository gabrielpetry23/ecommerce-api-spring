package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import java.time.LocalDate;

public record TrackingResponseDTO(
        String trackingCode,
        String carrier,
        String status,
        LocalDate estimatedDelivery
) {
}
