package io.github.gabrielpetry23.ecommerceapi.controller.dto;

public record OrderRequestDTO(
        String deliveryAddressId,
        String paymentMethodId
) {
}
