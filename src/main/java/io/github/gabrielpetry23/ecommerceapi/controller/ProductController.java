package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.ProductRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.ProductReviewDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.ProductReviewResponseDTO;
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

    @GetMapping("/{id}")
        public ResponseEntity<ProductRequestDTO> getById(@PathVariable("id") String id) {
        return service.findById(UUID.fromString(id))
                .map(product -> {
                    var dto = mapper.toDTO(product);
                    return ResponseEntity.ok(dto);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") String id, @RequestBody @Valid ProductRequestDTO dto) {
        var idProduct = UUID.fromString(id);
        Optional<Product> productOptional = service.findById(idProduct);

        if (productOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Category category = categoryService.findById(dto.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.categoryId()));

        var product = productOptional.get();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setCategory(category);
        product.setStock(dto.stock());

        service.update(product);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProductRequestDTO>> listAll() {
        var products = service.listAll();
        var dtos = products.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductRequestDTO>> search(
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
        Page<ProductRequestDTO> dtos = products.map(mapper::toDTO);
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

    @PreAuthorize("permitAll()")
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
}
