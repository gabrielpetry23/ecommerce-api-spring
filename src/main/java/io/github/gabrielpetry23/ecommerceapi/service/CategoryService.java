package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.ProductResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.ProductMapper;
import io.github.gabrielpetry23.ecommerceapi.exceptions.EntityNotFoundException;
import io.github.gabrielpetry23.ecommerceapi.model.Category;
import io.github.gabrielpetry23.ecommerceapi.repository.CategoryRepository;
import io.github.gabrielpetry23.ecommerceapi.validators.CategoryValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;
    private final CategoryValidator validator;
    private final ProductMapper productMapper;

    @Transactional
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

    @Transactional
    public void update(UUID uuid, Category category) {
        if (uuid == null) {
            throw new IllegalArgumentException("Category must exist to be updated");
        }
        validator.validateExistingCategoryId(uuid);
        validator.validateNewCategory(category);
        category.setId(uuid);
        repository.save(category);
    }

    @Transactional
    public void deleteById(String id) {
        Category category = repository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        repository.delete(category);
    }

    public List<ProductResponseDTO> findAllProductsDTOByCategoryId(UUID id) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        return category.getProducts()
                .stream()
                .map(productMapper::toDTO)
                .toList();
    }
}
