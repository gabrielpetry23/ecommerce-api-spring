package io.github.gabrielpetry23.ecommerceapi.controller.mappers;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.ProductReviewDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.ProductReviewResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.model.ProductReview;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ProductReviewResponseDTO toDTO(ProductReview productReview);

    ProductReview toEntity(ProductReviewDTO dto);
}
