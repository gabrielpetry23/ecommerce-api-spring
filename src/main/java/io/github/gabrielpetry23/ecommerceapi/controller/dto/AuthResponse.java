package io.github.gabrielpetry23.ecommerceapi.controller.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        Long expiresIn
) {
}
