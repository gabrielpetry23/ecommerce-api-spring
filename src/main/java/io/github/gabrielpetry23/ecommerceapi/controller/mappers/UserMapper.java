package io.github.gabrielpetry23.ecommerceapi.controller.mappers;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.AddressDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.PaymentMethodRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.UserDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.UserDetailsDTO;
import io.github.gabrielpetry23.ecommerceapi.model.Address;
import io.github.gabrielpetry23.ecommerceapi.model.PaymentMethod;
import io.github.gabrielpetry23.ecommerceapi.model.User;
import org.mapstruct.Mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserDTO dto);

    UserDetailsDTO toDTO(User user);
}
