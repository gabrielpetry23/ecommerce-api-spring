package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.ProductDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.ProductMapper;
import io.github.gabrielpetry23.ecommerceapi.model.Product;
import io.github.gabrielpetry23.ecommerceapi.service.ProductsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductsController implements GenericController{

//    === PRODUCTS ===
//    POST    /products             - Criar novo produto (GERENTE, ADMIN)
//    GET     /products/{id}         - Obter produto pelo ID (USER, GERENTE, ADMIN)
//    PUT     /products/{id}         - Atualizar produto (GERENTE, ADMIN)
//    DELETE  /products/{id}         - Deletar produto (GERENTE, ADMIN)
//    GET     /products/all          - Listar todos produtos (USER, GERENTE, ADMIN)
//    GET     /products/search       - Buscar produtos por nome/categoria (USER, GERENTE, ADMIN)

    private final ProductsService service;
    private final ProductMapper mapper;

    @PostMapping
//    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> create(@RequestBody @Valid ProductDTO dto) {
        Product product = mapper.toEntity(dto);
        service.save(product);
        //URI location = URI.create("/products/" + product.getId());
        URI location = generateHeaderLocation(product.getId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
        public ResponseEntity<ProductDTO> getById(@PathVariable("id") String id) {
        return service.findById(UUID.fromString(id))
                .map(product -> {
                    var dto = mapper.toDTO(product);
                    return ResponseEntity.ok(dto);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") String id, @RequestBody @Valid ProductDTO dto) {
        var idProduct = UUID.fromString(id);
        Optional<Product> productOptional = service.findById(idProduct);

        if (productOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var product = productOptional.get();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setCategory(dto.category());
        product.setStock(dto.stock());

        service.update(product);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> listAll() {
        var products = service.listAll();
        var dtos = products.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductDTO>> search(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "price", required = false) BigDecimal price,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        Page<Product> products = service.search(name, category, description, price, maxPrice, minPrice, page, pageSize);
        Page<ProductDTO> dtos = products.map(mapper::toDTO);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/adm-search")
    public ResponseEntity<Page<Product>> adminSearch(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "category", required = false) String category,
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
        Page<Product> products = service.admSearch(name, category, description, price, maxPrice, minPrice, stock, createdAt, updatedAt, id, page, pageSize);
        return ResponseEntity.ok(products);
    }
}
