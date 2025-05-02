package io.github.gabrielpetry23.ecommerceapi.controller.mappers;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.ProductRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductRequestDTO toDTO(Product product);

    Product toEntity(ProductRequestDTO productDTO);
}
