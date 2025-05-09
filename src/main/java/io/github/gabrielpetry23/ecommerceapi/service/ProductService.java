package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.*;
import io.github.gabrielpetry23.ecommerceapi.exceptions.EntityNotFoundException;
import io.github.gabrielpetry23.ecommerceapi.exceptions.InvalidFieldException;
import io.github.gabrielpetry23.ecommerceapi.model.*;
import io.github.gabrielpetry23.ecommerceapi.repository.ProductRepository;
import io.github.gabrielpetry23.ecommerceapi.repository.specs.ProductSpecs;
import io.github.gabrielpetry23.ecommerceapi.security.SecurityService;
import io.github.gabrielpetry23.ecommerceapi.validators.CategoryValidator;
import io.github.gabrielpetry23.ecommerceapi.validators.ProductValidator;
import io.github.gabrielpetry23.ecommerceapi.validators.UserValidator;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductValidator validator;
    private final ProductRepository repository;
    private final CategoryValidator categoryValidator;
    private final SecurityService securityService;
    private final ProductReviewService reviewService;
    private final CategoryService categoryService;
    private final ProductImageService productImageService;
    private final UserValidator userValidator;

    @Transactional
    public Product save(Product product) {
        validator.validateNewProduct(product);
        User currentUser = securityService.getCurrentUser();
        product.setUser(currentUser);
        return repository.save(product);
    }

    public Optional<Product> findById(UUID id) {
        return repository.findById(id);
    }

    public Page<Product> listAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }

    public Page<Product> search(String name, String categoryName, String description, BigDecimal price, BigDecimal maxPrice, BigDecimal minPrice, Integer stock, Integer page, Integer pageSize) {

        Specification<Product> specs = Specification.where((root, query, cb) -> cb.conjunction());

        if (name != null) {
            specs = specs.and(ProductSpecs.nameLike(name));
        }
        if (categoryName != null) {
            specs = specs.and(ProductSpecs.categoryNameEqual(categoryName));
        }
        if (description != null) {
            specs = specs.and(ProductSpecs.descriptionContainsKeywords(description));
        }
        if (price != null) {
            specs = specs.and(ProductSpecs.priceEqual(price));
        }

        if (minPrice != null) {
            specs = specs.and(ProductSpecs.priceGreaterThanOrEqualTo(minPrice));
        }

        if (maxPrice != null) {
            specs = specs.and(ProductSpecs.priceLessThanOrEqualTo(maxPrice));
        }

        if (stock != null) {
            specs = specs.and(ProductSpecs.stockGreaterThanOrEqualTo(stock));
        }

        return repository.findAll(specs, PageRequest.of(page, pageSize));
    }

    @Transactional
    public ProductReview addReview(UUID productId, ProductReviewDTO reviewDto) {
        Product product = repository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        ProductReview review = reviewService.createProductReviewForProduct(product, reviewDto);

        product.getReviews().add(review);
        repository.save(product);
        return review;
    }

    @Transactional
    public ProductImage addImage(UUID productId, ProductImageDTO imageDto) {

        Product product = repository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (imageDto.imageUrl() == null || imageDto.imageUrl().isEmpty()) {
            throw new InvalidFieldException("url", "Image URL cannot be null or empty");
        }

        if (imageDto.isMain()) {
            setMainImageFalse(product);
        }

        ProductImage image = productImageService.createImage(product, imageDto);
        product.getImages().add(image);
        repository.save(product);
        return image;
    }

    @Transactional
    public void deleteById(UUID uuid) {
        Product product = repository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        repository.delete(product);
    }

    @Transactional
    public void updateProduct(UUID id, ProductUpdateDTO dto) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        Category category = categoryService.findById(dto.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        if (!product.getCategory().getId().equals(category.getId())) {
            product.setCategory(category);
        }

        if (!dto.name().isBlank()) product.setName(dto.name());
        if (!dto.description().isBlank()) product.setDescription(dto.description());
        if (dto.price() != null) product.setPrice(dto.price());
        if (dto.stock() != null) product.setStock(dto.stock());

        categoryValidator.validateExistingCategoryId(category.getId());
        validator.validateNewProduct(product);

        repository.save(product);
    }

    @Transactional
    public void setMainImageFalse(Product product) {
        for (ProductImage image : product.getImages()) {
            if (image.isMain()) {
                image.setMain(false);
                productImageService.update(image);
            }
        }
    }

    @Transactional
    public void deleteImage(String id, String imageId) {
        Product product = repository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        ProductImage image = productImageService.findById(UUID.fromString(imageId))
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));

        product.getImages().remove(image);
        productImageService.delete(image);
        repository.save(product);
    }

    @Transactional
    public void deleteReview(String id, String reviewId) {
        ProductReview review = reviewService.findById(UUID.fromString(reviewId))
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        userValidator.validateCurrentUserAccessOrAdmin(review.getUser().getId());

        Product product = repository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        product.getReviews().remove(review);
        reviewService.delete(review);
        repository.save(product);
    }

    public List<ProductReviewResponseDTO> findAllProductReviewsDTOByProductId(String productId) {
        repository.findById(UUID.fromString(productId))
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        return reviewService.findAllProductReviewsDTOByProductId(UUID.fromString(productId));
    }

    public void validateExistingCategoryId(UUID categoryId) {
        categoryValidator.validateExistingCategoryId(categoryId);

    }
}
