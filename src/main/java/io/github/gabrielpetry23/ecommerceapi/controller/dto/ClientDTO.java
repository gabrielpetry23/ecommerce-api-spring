package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record ClientDTO(
        @NotBlank(message = "Client ID is required")
        String clientId,
        @NotBlank(message = "Client Secret is required")
        String clientSecret,
        @NotBlank(message = "Redirect URI is required")
        String redirectURI,
        @NotBlank(message = "Scope is required")
        String scope
) {
}
