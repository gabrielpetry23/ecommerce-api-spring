package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import io.github.gabrielpetry23.ecommerceapi.model.Product;

import java.util.List;

public record CategoryResponseDTO(
        String name,
        List<Product> products
) {

}
