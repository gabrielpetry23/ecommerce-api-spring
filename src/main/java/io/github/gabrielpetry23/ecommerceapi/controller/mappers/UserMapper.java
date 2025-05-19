package io.github.gabrielpetry23.ecommerceapi.controller.mappers;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.UserDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.UserDetailsDTO;
import io.github.gabrielpetry23.ecommerceapi.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AddressMapper.class, ProductMapper.class, PaymentMethodMapper.class, OrderMapper.class})
public interface UserMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "paymentMethods", ignore = true)
    User toEntity(UserDTO dto);

    @Mapping(source = "cart", target = "cart")
    @Mapping(source = "orders", target = "orders")
    @Mapping(source = "reviews", target = "reviews")
    @Mapping(source = "paymentMethods", target = "paymentMethods")
    UserDetailsDTO toDTO(User user);
}
