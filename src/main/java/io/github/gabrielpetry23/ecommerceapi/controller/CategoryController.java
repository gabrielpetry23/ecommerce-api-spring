package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.CategoryDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.ProductResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.CategoryMapper;
import io.github.gabrielpetry23.ecommerceapi.model.Category;
import io.github.gabrielpetry23.ecommerceapi.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Endpoints for managing categories")
public class CategoryController implements GenericController{

    private final CategoryService service;
    private final CategoryMapper mapper;

    @Operation(summary = "Create a new category", description = "Endpoint to create a new category. Requires MANAGER or ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created successfully",
                    headers = @Header(name = "Location", description = "URI of the created category", schema = @Schema(type = "string", format = "uri"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Object> save(@RequestBody CategoryDTO categoryDTO) {
        Category category = mapper.toEntity(categoryDTO);
        service.save(category);
        URI location = generateHeaderLocation(category.getId());
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "List all categories", description = "Endpoint to retrieve all categories. Accessible to all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of categories retrieved successfully")
    })
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<CategoryDTO>> listAll() {
        var categories = service.findAll();
        List<CategoryDTO> categoriesDtos = categories.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoriesDtos);
    }

    @Operation(summary = "Get category by ID", description = "Endpoint to retrieve a specific category by its ID. Accessible to all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "400", description = "Invalid category ID format"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<CategoryDTO> findById(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the category to retrieve", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("id") String id
    ) {
        return service.findById(UUID.fromString(id))
                .map(category -> ResponseEntity.ok(mapper.toDTO(category)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get products by category ID", description = "Endpoint to retrieve all products associated with a specific category. Accessible to all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid category ID format"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{id}/products")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProductResponseDTO>> findProductsByCategory(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the category to retrieve products for", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("id") String id
    ) {
        List<ProductResponseDTO> productsDTOs = service.findAllProductsDTOByCategoryId(UUID.fromString(id));
        return ResponseEntity.ok(productsDTOs);
    }

    @Operation(summary = "Update a category", description = "Endpoint to update the details of an existing category. Requires MANAGER or ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> update(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the category to update", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("id") String id,
            @RequestBody CategoryDTO categoryDTO
    ) {
        var category = mapper.toEntity(categoryDTO);
        service.update(UUID.fromString(id), category);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a category", description = "Endpoint to delete a specific category by its ID. Requires MANAGER or ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid category ID format"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> delete(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the category to delete", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("id") String id
    ) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
