package io.github.gabrielpetry23.ecommerceapi.validators;

import io.github.gabrielpetry23.ecommerceapi.exceptions.InvalidFieldException;
import io.github.gabrielpetry23.ecommerceapi.exceptions.ResourceNotFoundException;
import io.github.gabrielpetry23.ecommerceapi.model.Category;
import io.github.gabrielpetry23.ecommerceapi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CategoryValidator {

    private final CategoryRepository categoryRepository;

    public void validateNewCategory(Category category) {
        if (category.getName() == null || category.getName().isBlank()) {
            throw new InvalidFieldException("name", "Category name is required.");
        }

        if (existsCategoryWithTheSameName(category)) {
            throw new InvalidFieldException("name", "Category name already exists.");
        }
    }

    private boolean existsCategoryWithTheSameName(Category category) {
        Optional<Category> existingCategory = categoryRepository.findByName(category.getName());
        return existingCategory.isPresent();
    }

    public void validateExistingCategoryId(UUID categoryId) {
        if (categoryId == null) {
            throw new InvalidFieldException("categoryId", "Category ID is required.");
        }

        Optional<Category> category = categoryRepository.findById(categoryId);

        if (category.isEmpty()) {
            throw new ResourceNotFoundException("Category not found.");
        }
    }
}