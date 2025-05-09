package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.ProductReviewDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.ProductReviewResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.ReviewMapper;
import io.github.gabrielpetry23.ecommerceapi.model.Product;
import io.github.gabrielpetry23.ecommerceapi.model.ProductReview;
import io.github.gabrielpetry23.ecommerceapi.model.User;
import io.github.gabrielpetry23.ecommerceapi.repository.ProductReviewRepository;
import io.github.gabrielpetry23.ecommerceapi.security.SecurityService;
import io.github.gabrielpetry23.ecommerceapi.validators.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private final ProductReviewRepository repository;
    private final SecurityService securityService;
    private final UserValidator userValidator;
    private final ReviewMapper mapper;

    public ProductReview createProductReviewForProduct(Product product, ProductReviewDTO dto) {

        ProductReview review = new ProductReview();

        review.setRating(dto.rating());
        review.setComment(dto.comment());
        review.setProduct(product);

        User currentUser = securityService.getCurrentUser();
        review.setUser(currentUser);

        return repository.save(review);
    }

    public List<ProductReviewResponseDTO> findAllProductReviewsDTOByProductId(UUID productId) {
        return repository.findAllByProductId(productId)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public void delete(ProductReview review) {
        if (review == null) {
            throw new IllegalArgumentException("Review must exist to be deleted");
        }

        userValidator.validateCurrentUserAccess(review.getUser().getId());
        repository.delete(review);
    }

    public Optional<ProductReview> findById(UUID id) {
        return repository.findById(id);
    }
}
