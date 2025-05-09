package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.*;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.ProductMapper;
import io.github.gabrielpetry23.ecommerceapi.exceptions.ResourceNotFoundException;
import io.github.gabrielpetry23.ecommerceapi.model.Category;
import io.github.gabrielpetry23.ecommerceapi.model.Product;
import io.github.gabrielpetry23.ecommerceapi.model.ProductImage;
import io.github.gabrielpetry23.ecommerceapi.model.ProductReview;
import io.github.gabrielpetry23.ecommerceapi.service.CategoryService;
import io.github.gabrielpetry23.ecommerceapi.service.ProductReviewService;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController implements GenericController{

//    PRODUTOS
//========
//    POST   /products                        Criar um novo produto                         [ADMIN, MANAGER]
//    GET    /products                        Listar todos os produtos                      [Público]
//    GET    /products/{id}                   Obter um produto específico                   [Público]
//    PUT    /products/{id}                   Atualizar um produto                          [ADMIN, MANAGER]
//    DELETE /products/{id}                   Excluir um produto                            [ADMIN, MANAGER]
//    GET    /products/search                 Buscar produtos por nome/categoria            [Público]
//    GET    /products/{id}/reviews           Obter as reviews de um produto                [Público]
//    POST   /products/{id}/reviews           Criar uma review                              [USER]
//    DELETE /products/{id}/reviews/{reviewId}  Excluir uma review                          [USER (próprio), ADMIN, MANAGER]
//    POST   /products/{id}/images            Adicionar imagem ao produto                   [ADMIN, MANAGER]
//    DELETE /products/{id}/images/{imageId}  Remover imagem do produto                     [ADMIN, MANAGER]

    private final ProductService service;
    private final ProductMapper mapper;
    private final CategoryValidator categoryValidator;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> create(@RequestBody @Valid ProductRequestDTO dto) {
        categoryValidator.validateExistingCategoryId(dto.categoryId());
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
    public ResponseEntity<List<ProductResponseDTO>> listAll() {
        var products = service.listAll();
        var dtos = products.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
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
//        return service.findById(UUID.fromString(id))
//                .map(product -> {
//                    ProductReview review = service.addReview(product, dto);
//                    URI location = generateNestedHeaderLocation(product.getId(), "reviews", review.getId());
//                    return ResponseEntity.created(location).build();
//                })
//                .orElseGet(() -> ResponseEntity.notFound().build());

        ProductReview review = service.addReview(UUID.fromString(id), dto);
        URI location = generateNestedHeaderLocation(review.getProduct().getId(), "reviews", review.getId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}/reviews")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProductReviewResponseDTO>> getReviews(@PathVariable("id") String id) {
//        if (!service.existsById(UUID.fromString(id))) {
//            return ResponseEntity.notFound().build();
//        }
//        List<ProductReviewResponseDTO> reviewsDto = reviewService.findAllProductReviewsDTOByProductId(UUID.fromString(id));
//        return ResponseEntity.ok(reviewsDto);
        List<ProductReviewResponseDTO> reviewsDto = service.findAllProductReviewsDTOByProductId(id);
        return ResponseEntity.ok(reviewsDto);
    }

    @DeleteMapping("/{id}/reviews/{reviewId}")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public ResponseEntity<Object> deleteReview(@PathVariable("id") String id, @PathVariable("reviewId") String reviewId) {
        System.out.println("TESTEEEE");
        service.deleteReview(id, reviewId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> addImage(@PathVariable("id") String id, @RequestBody @Valid ProductImageDTO dto) {
//        return service.findById(UUID.fromString(id))
//                .map(product -> {
//                    ProductImage img = service.addImage(product, dto);
//                    URI location = generateNestedHeaderLocation(product.getId(), "images", img.getId());
//                    return ResponseEntity.created(location).build();
//                })
//                .orElseGet(() -> ResponseEntity.notFound().build());
        ProductImage image = service.addImage(UUID.fromString(id), dto);
        URI location = generateNestedHeaderLocation(image.getProduct().getId(), "images", image.getId());
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{id}/images/{imageId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> removeImage(@PathVariable("id") String id, @PathVariable("imageId") String imageId) {
//        return service.findById(UUID.fromString(id))
//                .map(product -> {
//                    service.removeImage(product, UUID.fromString(imageId));
//                    return ResponseEntity.noContent().build();
//                })
//                .orElseGet(() -> ResponseEntity.notFound().build());
        service.deleteImage(id, imageId);
        return ResponseEntity.noContent().build();
    }
}
