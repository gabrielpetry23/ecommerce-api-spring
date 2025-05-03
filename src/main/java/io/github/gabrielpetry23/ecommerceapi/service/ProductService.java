package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.ProductImageDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.ProductReviewDTO;
import io.github.gabrielpetry23.ecommerceapi.exceptions.InvalidFieldException;
import io.github.gabrielpetry23.ecommerceapi.model.*;
import io.github.gabrielpetry23.ecommerceapi.repository.ProductRepository;
import io.github.gabrielpetry23.ecommerceapi.repository.specs.ProductSpecs;
import io.github.gabrielpetry23.ecommerceapi.security.SecurityService;
import io.github.gabrielpetry23.ecommerceapi.validators.CategoryValidator;
import io.github.gabrielpetry23.ecommerceapi.validators.ProductValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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

    public Product save(Product product) {
        validator.validateNewProduct(product);
        User currentUser = securityService.getCurrentUser();
        product.setUser(currentUser);
        return repository.save(product);
    }

    public Optional<Product> findById(UUID id) {
        return repository.findById(id);
    }

    public void update(Product product) {
        if (product.getId() == null) {
            throw new IllegalArgumentException("Product must exist to be updated");
        }
        categoryValidator.validateExistingCategoryId(product.getCategory().getId());
        validator.validateNewProduct(product);
        repository.save(product);
    }

    public List<Product> listAll() {
        return repository.findAll();
    }

    public Page<Product> search(String name, String categoryName, String description, BigDecimal price, BigDecimal maxPrice, BigDecimal minPrice, Integer page, Integer pageSize) {

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

        return repository.findAll(specs, PageRequest.of(page, pageSize));
    }

    public Page<Product> admSearch(String name, String categoryName, String description, BigDecimal price, BigDecimal maxPrice, BigDecimal minPrice, Integer stock, String createdAt, String updatedAt, UUID id, Integer page, Integer pageSize) {

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

        if (createdAt != null) {
            specs = specs.and(ProductSpecs.createdAtContains(createdAt.toString()));
        }

        if (updatedAt != null) {
            specs = specs.and(ProductSpecs.updatedAtContains(updatedAt.toString()));
        }

        if (id != null) {
            specs = specs.and(ProductSpecs.idEqual(id));
        }

        return repository.findAll(specs, PageRequest.of(page, pageSize));
    }

    public void delete(Product product) {
        if (product.getId() == null) {
            throw new IllegalArgumentException("Product must exist to be deleted");
        }
        repository.delete(product);
    }

    public void addReview(Product product, ProductReviewDTO reviewDTO) {
        if (product.getId() == null) {
            throw new IllegalArgumentException("Product must exist to add a review");
        }

        if (reviewDTO.rating() == null || reviewDTO.rating() < 1 || reviewDTO.rating() > 10) {
            throw new InvalidFieldException("rating", "Rating must be between 1 and 10");
        }

        var review = new ProductReview();
        review.setRating(reviewDTO.rating());
        review.setComment(reviewDTO.comment());
        review.setProduct(product);

        User currentUser = securityService.getCurrentUser();
        review.setUser(currentUser);

        product.getReviews().add(review);
        repository.save(product);
    }

    public List<ProductReview> findReviewsByProduct(Product product) {
        return product.getReviews();
    }

    public void addImage(Product product, ProductImageDTO dto) {
        if (product.getId() == null) {
            throw new IllegalArgumentException("Product must exist to add an image");
        }

        if (dto.imageUrl() == null || dto.imageUrl().isEmpty()) {
            throw new InvalidFieldException("url", "Image URL cannot be null or empty");
        }

        var image = new ProductImage();
        image.setImageUrl(dto.imageUrl());
        image.setMain(dto.isMain());
        image.setProduct(product);
        product.getImages().add(image);
        repository.save(product);
    }

    public void removeImage(Product product, UUID imageId) {
        if (product.getId() == null) {
            throw new IllegalArgumentException("Product must exist to remove an image");
        }

        var image = product.getImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Image not found"));

        product.getImages().remove(image);
        repository.save(product);

    }
}
