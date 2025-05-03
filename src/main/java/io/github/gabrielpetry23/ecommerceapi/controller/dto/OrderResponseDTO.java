package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import io.github.gabrielpetry23.ecommerceapi.model.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponseDTO(
        UserNameIdDTO user,
        List<OrderItemsResponseDTO> orderItems,
        BigDecimal totalPrice,
        OrderStatus status
) {
}
