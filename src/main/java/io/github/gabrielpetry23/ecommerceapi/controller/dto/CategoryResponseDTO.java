package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import java.util.List;

public record CategoryResponseDTO(
        String name,
        List<ProductResponseDTO> products
) {

}
