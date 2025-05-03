package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import io.github.gabrielpetry23.ecommerceapi.model.Address;
import io.github.gabrielpetry23.ecommerceapi.model.PaymentMethod;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UserUpdateDTO(
        String name,
        String email,
        String password,
        String role,
        List<AddressDTO> addresses,
        List<PaymentMethodDTO> paymentMethods

) {
}
