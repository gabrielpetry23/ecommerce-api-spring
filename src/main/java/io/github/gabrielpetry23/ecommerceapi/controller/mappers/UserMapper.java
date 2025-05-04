package io.github.gabrielpetry23.ecommerceapi.controller.mappers;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.AddressDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.PaymentMethodDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.UserDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.UserDetailsDTO;
import io.github.gabrielpetry23.ecommerceapi.model.Address;
import io.github.gabrielpetry23.ecommerceapi.model.PaymentMethod;
import io.github.gabrielpetry23.ecommerceapi.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserDTO dto);

    UserDetailsDTO toDTO(User user);

    Address toEntity(AddressDTO dto);

    AddressDTO toDTO(Address address);

    PaymentMethodDTO toDTO(PaymentMethod paymentMethod);

    PaymentMethod toEntity(PaymentMethodDTO dto);
}
