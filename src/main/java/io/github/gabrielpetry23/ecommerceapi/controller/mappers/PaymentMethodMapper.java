package io.github.gabrielpetry23.ecommerceapi.controller.mappers;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.PaymentMethodRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.PaymentMethodResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.model.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface PaymentMethodMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    PaymentMethod toEntity(PaymentMethodRequestDTO dto);

    PaymentMethodResponseDTO toDTO(PaymentMethod paymentMethod);

    default LocalDate stringToLocalDate(String expiryDateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(expiryDateString, formatter);
    }
}
