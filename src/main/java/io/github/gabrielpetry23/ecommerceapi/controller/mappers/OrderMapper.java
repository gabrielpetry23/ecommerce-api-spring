package io.github.gabrielpetry23.ecommerceapi.controller.mappers;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.OrderItemsResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.OrderResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.model.Order;
import io.github.gabrielpetry23.ecommerceapi.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper { ;

    @Mapping(source = "total", target = "totalPrice")
    @Mapping(source = "items", target = "orderItems")
    OrderResponseDTO toDTO(Order order);

    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "price", target = "itemPrice")
    OrderItemsResponseDTO toOrderItemsResponseDTO(OrderItem orderItem);
}
