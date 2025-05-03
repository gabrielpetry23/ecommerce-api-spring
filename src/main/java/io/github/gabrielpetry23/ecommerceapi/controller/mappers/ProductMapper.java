package io.github.gabrielpetry23.ecommerceapi.controller.mappers;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.ProductRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.ProductResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.ProductReviewDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.ProductReviewResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.model.Product;
import io.github.gabrielpetry23.ecommerceapi.model.ProductReview;
import io.github.gabrielpetry23.ecommerceapi.repository.CategoryRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public abstract class ProductMapper {

    @Autowired
    CategoryRepository categoryRepository;

    @Mapping(target = "category", expression = "java(categoryRepository.findById(dto.categoryId()).orElse(null))")
    public abstract Product toEntity(ProductRequestDTO dto);

    @Mapping(source = "category.id", target = "categoryId")
//    @Mapping(source = "category.name", target = "categoryName")
//    @Mapping(source = "category", target = "category")
    public abstract ProductResponseDTO toDTO(Product entity);

    public abstract ProductReview toEntity(ProductReviewDTO dto);

    @Mapping(source = "user.name", target = "userName")
    public abstract ProductReviewResponseDTO toDTO(ProductReview entity);
}
