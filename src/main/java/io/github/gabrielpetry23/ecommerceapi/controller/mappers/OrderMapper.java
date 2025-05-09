package io.github.gabrielpetry23.ecommerceapi.controller.mappers;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.OrderItemsResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.OrderResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.UserNameIdDTO;
import io.github.gabrielpetry23.ecommerceapi.model.Order;
import io.github.gabrielpetry23.ecommerceapi.model.OrderItem;
import io.github.gabrielpetry23.ecommerceapi.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper { ;

    @Mapping(source = "total", target = "totalPrice")
    @Mapping(source = "items", target = "orderItems")
    OrderResponseDTO toDTO(Order order);

    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "price", target = "itemPrice")
    OrderItemsResponseDTO toOrderItemsResponseDTO(OrderItem orderItem);
}
