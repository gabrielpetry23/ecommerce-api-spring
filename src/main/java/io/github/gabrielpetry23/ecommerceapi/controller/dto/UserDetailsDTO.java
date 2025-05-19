package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import java.util.List;

public record UserDetailsDTO(
    String name,
    String email,
    String password,
    String role,
    CartResponseDTO cart,
    List<OrderResponseDTO> orders,
    List<ProductReviewResponseDTO> reviews,
    List<AddressDTO> addresses,
    List<PaymentMethodRequestDTO> paymentMethods
) {
}
