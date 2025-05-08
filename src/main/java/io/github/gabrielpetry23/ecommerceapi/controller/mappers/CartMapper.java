package io.github.gabrielpetry23.ecommerceapi.controller.mappers;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.CartResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.model.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartResponseDTO toDTO(Cart cart);

    // Cart toEntity(CartDTO cartDTO);

    // CartItem toEntity(CartItemRequestDTO cartItemDTO);

    // CartItemRequestDTO toDTO(CartItem cartItem);
}
