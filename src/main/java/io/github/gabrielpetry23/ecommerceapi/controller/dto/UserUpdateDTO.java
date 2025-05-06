package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import java.util.List;

public record UserUpdateDTO(
        String name,
        String email,
        String password
) {
}
