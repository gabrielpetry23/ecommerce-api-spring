package io.github.gabrielpetry23.ecommerceapi.controller.mappers;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.CouponDTO;
import io.github.gabrielpetry23.ecommerceapi.model.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "validUntil", expression = "java(LocalDate.parse(dto.validUntil(), DateTimeFormatter.ofPattern(\"yyyy-MM-dd\")))")
    Coupon toEntity(CouponDTO dto);

    CouponDTO toDTO(Coupon coupon);

//    default LocalDate stringToLocalDate(String expiryDateString) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        return LocalDate.parse(expiryDateString, formatter);
//    }
}
