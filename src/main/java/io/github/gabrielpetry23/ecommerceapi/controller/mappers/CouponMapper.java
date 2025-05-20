package io.github.gabrielpetry23.ecommerceapi.controller.mappers;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.CouponDTO;
import io.github.gabrielpetry23.ecommerceapi.model.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "validUntil", source = "validUntil", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "isActive", expression = "java(dto.isActive() != null ? dto.isActive() : true)")
    Coupon toEntity(CouponDTO dto);

    CouponDTO toDTO(Coupon coupon);

    @Named("stringToLocalDate")
    default LocalDate stringToLocalDate(String expiryDateString) {
        if (expiryDateString == null || expiryDateString.trim().isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(expiryDateString, formatter);
    }
}
