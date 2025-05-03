package io.github.gabrielpetry23.ecommerceapi.controller.dto;

public record AddressDTO(
        String street,
        String number,
        String complement,
        String city,
        String state,
        String zipCode,
        String country
) {
}
