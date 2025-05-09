package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.AddressDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.CategoryDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.CategoryResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.ProductResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.CategoryMapper;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.ProductMapper;
import io.github.gabrielpetry23.ecommerceapi.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;
    private final CategoryMapper mapper;
    private final ProductMapper productMapper;


//    CATEGORIAS
//==========
//    GET    /categories                      Listar todas as categorias                    [Público]
//    GET    /categories/{id}                 Obter uma categoria específica                [Público]
//   GET    /categories/{id}/products         Listar produtos de uma categoria              [Público]
//    POST   /categories                      Criar uma categoria                           [ADMIN, MANAGER]
//    PUT    /categories/{id}                 Atualizar uma categoria                       [ADMIN, MANAGER]
//    DELETE /categories/{id}                 Excluir uma categoria                         [ADMIN, MANAGER]

    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Object> save(@RequestBody CategoryDTO categoryDTO) {
        var category = mapper.toEntity(categoryDTO);
        service.save(category);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<CategoryDTO>> listAll() {
        var categories = service.findAll();
        List<CategoryDTO> categoriesDtos = categories.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoriesDtos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<CategoryDTO> findById(@PathVariable("id") String id) {
        return service.findById(UUID.fromString(id))
                .map(category -> ResponseEntity.ok(mapper.toDTO(category)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/products")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProductResponseDTO>> findProductsByCategory(@PathVariable("id") String id) {
        List<ProductResponseDTO> productsDTOs = service.findAllProductsDTOByCategoryId(UUID.fromString(id));
        return ResponseEntity.ok(productsDTOs);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> update(@PathVariable("id") String id, @RequestBody CategoryDTO categoryDTO) {
        var category = mapper.toEntity(categoryDTO);
        service.update(UUID.fromString(id), category);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> delete(@PathVariable("id") String id) {
//        return service.findById(UUID.fromString(id))
//                .map(category -> {
//                    service.delete(category);
//                    return ResponseEntity.noContent().build();
//                }).orElseGet(() -> ResponseEntity.notFound().build());
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
