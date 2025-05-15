package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import io.github.gabrielpetry23.ecommerceapi.model.Address;
import io.github.gabrielpetry23.ecommerceapi.model.PaymentMethod;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UserDTO(

        @NotBlank(message = "required field")
        String name,
        @Email(message = "invalid")
        String email,
        @NotBlank(message = "required field")
        String password,
        //@ValidRole
        String role
) {
}
