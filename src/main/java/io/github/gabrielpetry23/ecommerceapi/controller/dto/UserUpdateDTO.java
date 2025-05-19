package io.github.gabrielpetry23.ecommerceapi.controller.dto;

public record UserUpdateDTO(
        String name,
        String email,
        String password
) {
}
