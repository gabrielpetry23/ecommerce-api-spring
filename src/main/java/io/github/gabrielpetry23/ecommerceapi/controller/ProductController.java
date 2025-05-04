package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.*;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.ProductMapper;
import io.github.gabrielpetry23.ecommerceapi.exceptions.ResourceNotFoundException;
import io.github.gabrielpetry23.ecommerceapi.model.Category;
import io.github.gabrielpetry23.ecommerceapi.model.Product;
import io.github.gabrielpetry23.ecommerceapi.model.ProductReview;
import io.github.gabrielpetry23.ecommerceapi.service.CategoryService;
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
//    GET    /products/adm-search             Buscar produtos por nome/categoria (admin)    [ADMIN, MANAGER]
//    GET    /products/{id}/reviews           Obter as reviews de um produto                [Público]
//    POST   /products/{id}/reviews           Criar uma review                              [USER]
//    POST   /products/{id}/images            Adicionar imagem ao produto                   [ADMIN, MANAGER]
//    DELETE /products/{id}/images/{imageId}  Remover imagem do produto                     [ADMIN, MANAGER]

    private final ProductService service;
    private final ProductMapper mapper;
    private final CategoryService categoryService;
    private final CategoryValidator categoryValidator;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> create(@RequestBody @Valid ProductRequestDTO dto) {
        categoryValidator.validateExistingCategoryId(dto.categoryId());
        Product product = mapper.toEntity(dto);
        service.save(product);
        //URI location = URI.create("/products/" + product.getId());
        URI location = generateHeaderLocation(product.getId());
        return ResponseEntity.created(location).build();
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{id}")
        public ResponseEntity<ProductResponseDTO> getById(@PathVariable("id") String id) {
        return service.findById(UUID.fromString(id))
                .map(product -> {
                    var dto = mapper.toDTO(product);
                    return ResponseEntity.ok(dto);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") String id, @RequestBody ProductUpdateDTO dto) {
        Optional<Product> productOptional = service.findById(UUID.fromString(id));

        if (productOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Category category = categoryService.findById(dto.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.categoryId()));

        var product = productOptional.get();

        if (product.getCategory() != null && !product.getCategory().getId().equals(category.getId())) {
            product.setCategory(category);
        }

        if (dto.name() != null && !dto.name().isBlank()) {
            product.setName(dto.name());
        }

        if (dto.description() != null && !dto.description().isBlank()) {
            product.setDescription(dto.description());
        }

        if (dto.price() != null) {
            product.setPrice(dto.price());
        }

        if (dto.stock() != null) {
            product.setStock(dto.stock());
        }

        service.update(product);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("permitAll()")
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> listAll() {
        var products = service.listAll();
        var dtos = products.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDTO>> search(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "category", required = false) String categoryName,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "price", required = false) BigDecimal price,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        Page<Product> products = service.search(name, categoryName, description, price, maxPrice, minPrice, page, pageSize);
        Page<ProductResponseDTO> dtos = products.map(mapper::toDTO);
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @GetMapping("/adm-search")
    public ResponseEntity<Page<Product>> adminSearch(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "category", required = false) String categoryName,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "price", required = false) BigDecimal price,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "stock", required = false) Integer stock,
            @RequestParam(value = "createdAt", required = false) String createdAt,
            @RequestParam(value = "updatedAt", required = false) String updatedAt,
            @RequestParam(value = "id", required = false) UUID id,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        Page<Product> products = service.admSearch(name, categoryName, description, price, maxPrice, minPrice, stock, createdAt, updatedAt, id, page, pageSize);
        return ResponseEntity.ok(products);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") String id) {
        return service.findById(UUID.fromString(id))
                .map(product -> {
                    service.delete(product);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{id}/reviews")
    public ResponseEntity<Object> createReview(@PathVariable("id") String id, @RequestBody @Valid ProductReviewDTO dto) {
        return service.findById(UUID.fromString(id))
                .map(product -> {
                    service.addReview(product, dto);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ProductReviewResponseDTO>> getReviews(@PathVariable("id") String id) {
        return service.findById(UUID.fromString(id))
                .map(product -> {
                    List<ProductReviewResponseDTO> reviews = service.findReviewsByProduct(product)
                            .stream()
                            .map(mapper::toDTO)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(reviews);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PostMapping("/{id}/images")
    public ResponseEntity<Object> addImage(@PathVariable("id") String id, @RequestBody @Valid ProductImageDTO dto) {
        return service.findById(UUID.fromString(id))
                .map(product -> {
                    service.addImage(product, dto);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @DeleteMapping("/{id}/images/{imageId}")
    public ResponseEntity<Object> removeImage(@PathVariable("id") String id, @PathVariable("imageId") String imageId) {
        return service.findById(UUID.fromString(id))
                .map(product -> {
                    service.removeImage(product, UUID.fromString(imageId));
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
