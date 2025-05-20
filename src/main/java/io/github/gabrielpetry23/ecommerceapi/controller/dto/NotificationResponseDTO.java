package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponseDTO(
        UUID id,
        UUID userId,
        String type,
        String content,
        LocalDateTime readAt
) {
}
