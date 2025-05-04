package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressResponseDTO(
        String street,
        String number,
        String complement,
        String city,
        String state,
        String zipCode,
        String country
) {
}
