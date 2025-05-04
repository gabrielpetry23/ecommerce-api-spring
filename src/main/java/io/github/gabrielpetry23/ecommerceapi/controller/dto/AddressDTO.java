package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressDTO(
        @NotBlank(message = "Street is required")
        String street,
        @NotBlank(message = "Number is required")
        String number,
        String complement,
        @NotBlank(message = "City is required")
        String city,
        @NotBlank(message = "State is required")
        String state,
        @NotBlank(message = "Zip code is required")
        String zipCode,
        @NotBlank(message = "Country is required")
        String country
) {
}
