package io.github.gabrielpetry23.ecommerceapi.controller.mappers;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.PaymentMethodRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.PaymentMethodResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.model.PaymentMethod;
import org.mapstruct.Mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface PaymentMethodMapper {

    PaymentMethod toEntity(PaymentMethodRequestDTO dto);

    PaymentMethodResponseDTO toDTO(PaymentMethod paymentMethod);

    default LocalDate stringToLocalDate(String expiryDateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(expiryDateString, formatter);
    }
}
