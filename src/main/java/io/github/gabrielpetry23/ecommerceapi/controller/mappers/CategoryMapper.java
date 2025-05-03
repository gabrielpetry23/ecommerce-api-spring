package io.github.gabrielpetry23.ecommerceapi.controller.mappers;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.CategoryDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.CategoryResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponseDTO toDTO(Category category);

    Category toEntity(CategoryDTO categoryDTO);
}
