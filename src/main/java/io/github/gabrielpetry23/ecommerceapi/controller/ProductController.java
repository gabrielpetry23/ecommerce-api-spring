package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.*;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.ProductMapper;
import io.github.gabrielpetry23.ecommerceapi.model.Product;
import io.github.gabrielpetry23.ecommerceapi.model.ProductImage;
import io.github.gabrielpetry23.ecommerceapi.model.ProductReview;
import io.github.gabrielpetry23.ecommerceapi.service.ProductService;
import io.github.gabrielpetry23.ecommerceapi.validators.CategoryValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController implements GenericController {

    private final ProductService service;
    private final ProductMapper mapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> create(@RequestBody @Valid ProductRequestDTO dto) {
        service.validateExistingCategoryId(dto.categoryId());
        Product product = mapper.toEntity(dto);
        service.save(product);
        URI location = generateHeaderLocation(product.getId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable("id") String id) {
        return service.findById(UUID.fromString(id))
                .map(product -> {
                    var dto = mapper.toDTO(product);
                    return ResponseEntity.ok(dto);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> update(@PathVariable("id") String id, @RequestBody ProductUpdateDTO dto) {
        service.updateProduct(UUID.fromString(id), dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<ProductResponseDTO>> listAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<Product> productsPage = service.listAll(page, size);
        Page<ProductResponseDTO> dtoPage = productsPage.map(mapper::toDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/search")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<ProductResponseDTO>> search(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "category", required = false) String categoryName,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "price", required = false) BigDecimal price,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "stock", required = false) Integer stock,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        Page<Product> products = service.search(name, categoryName, description, price, maxPrice, minPrice, stock, page, pageSize);
        Page<ProductResponseDTO> dtos = products.map(mapper::toDTO);
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> delete(@PathVariable("id") String id) {
        service.deleteById(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reviews")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> createReview(@PathVariable("id") String id, @RequestBody @Valid ProductReviewDTO dto) {
        ProductReview review = service.addReview(UUID.fromString(id), dto);
        URI location = generateNestedHeaderLocation(review.getProduct().getId(), "reviews", review.getId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}/reviews")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProductReviewResponseDTO>> getReviews(@PathVariable("id") String id) {
        List<ProductReviewResponseDTO> reviewsDto = service.findAllProductReviewsDTOByProductId(id);
        return ResponseEntity.ok(reviewsDto);
    }

    @DeleteMapping("/{id}/reviews/{reviewId}")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public ResponseEntity<Object> deleteReview(@PathVariable("id") String id, @PathVariable("reviewId") String reviewId) {
        service.deleteReview(id, reviewId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> addImage(@PathVariable("id") String id, @RequestBody @Valid ProductImageDTO dto) {
        ProductImage image = service.addImage(UUID.fromString(id), dto);
        URI location = generateNestedHeaderLocation(image.getProduct().getId(), "images", image.getId());
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{id}/images/{imageId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> removeImage(@PathVariable("id") String id, @PathVariable("imageId") String imageId) {
        service.deleteImage(id, imageId);
        return ResponseEntity.noContent().build();
    }
}
