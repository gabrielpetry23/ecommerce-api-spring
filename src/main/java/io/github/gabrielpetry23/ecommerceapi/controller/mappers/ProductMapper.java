package io.github.gabrielpetry23.ecommerceapi.controller.mappers;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.*;
import io.github.gabrielpetry23.ecommerceapi.model.Product;
import io.github.gabrielpetry23.ecommerceapi.model.ProductImage;
import io.github.gabrielpetry23.ecommerceapi.model.ProductReview;
import io.github.gabrielpetry23.ecommerceapi.repository.CategoryRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public abstract class ProductMapper {

    @Autowired
    CategoryRepository categoryRepository;

    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", expression = "java(categoryRepository.findById(dto.categoryId()).orElse(null))")
    @Mapping(target = "user", ignore = true)
    public abstract Product toEntity(ProductRequestDTO dto);

    @Mapping(source = "category.id", target = "category.id")
    @Mapping(source = "category.name", target = "category.name")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "images", target = "images", qualifiedByName = "mapImages")
    public abstract ProductResponseDTO toDTO(Product entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    public abstract ProductReview toEntity(ProductReviewDTO dto);

    @Mapping(source = "user.id", target = "user.id")
    @Mapping(source = "user.name", target = "user.name")
    public abstract ProductReviewResponseDTO toDTO(ProductReview entity);

    @Mapping(source = "imageUrl", target = "imageUrl")
    @Mapping(source = "main", target = "isMain")
    public abstract ProductImageDTO toImageDTO(ProductImage image);

    @Named("mapImages")
    public List<ProductImageDTO> mapImages(List<ProductImage> images) {
        return images.stream()
                .map(this::toImageDTO)
                .toList();
    }
}
