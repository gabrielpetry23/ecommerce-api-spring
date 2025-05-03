package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.model.Category;
import io.github.gabrielpetry23.ecommerceapi.model.Product;
import io.github.gabrielpetry23.ecommerceapi.repository.CategoryRepository;
import io.github.gabrielpetry23.ecommerceapi.validators.CategoryValidator;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;
    private final CategoryValidator validator;

    public Category save(Category category) {
        validator.validateNewCategory(category);
        return repository.save(category);
    }

    public Optional<Category> findById(UUID uuid) {
        return repository.findById(uuid);
    }

    public List<Category> findAll() {
        return repository.findAll();
    }

    public void update(UUID uuid, Category category) {
        if (uuid == null) {
            throw new IllegalArgumentException("Category must exist to be updated");
        }
        validator.validateExistingCategoryId(uuid);
        validator.validateNewCategory(category);
        category.setId(uuid);
        repository.save(category);
    }

    public void delete(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category must exist to be deleted");
        }
        repository.delete(category);
    }
}
