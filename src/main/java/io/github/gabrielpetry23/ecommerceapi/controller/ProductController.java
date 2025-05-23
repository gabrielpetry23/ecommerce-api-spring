package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.*;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.ProductMapper;
import io.github.gabrielpetry23.ecommerceapi.model.Product;
import io.github.gabrielpetry23.ecommerceapi.model.ProductImage;
import io.github.gabrielpetry23.ecommerceapi.model.ProductReview;
import io.github.gabrielpetry23.ecommerceapi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Endpoints for managing products in the system")
public class ProductController implements GenericController {

    private final ProductService service;
    private final ProductMapper mapper;

    @Operation(summary = "Create a new product", description = "Endpoint to create a new product in the system. Requires MANAGER or ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    headers = @Header(name = "Location", description = "URI of the created product", schema = @Schema(type = "string", format = "uri"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> create(@RequestBody @Valid ProductRequestDTO dto) {
        service.validateExistingCategoryId(dto.categoryId());
        Product product = mapper.toEntity(dto);
        Product savedProduct = service.save(product);
        URI location = generateHeaderLocation(savedProduct.getId());
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Get product by ID", description = "Endpoint to retrieve a specific product based on its ID. Accessible to all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "400", description = "Invalid product ID format"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ProductResponseDTO> getById(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the product to retrieve", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("id") String id
    ) {
        return service.findById(UUID.fromString(id))
                .map(product -> {
                    var dto = mapper.toDTO(product);
                    return ResponseEntity.ok(dto);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update an existing product", description = "Endpoint to update the details of an existing product. Requires MANAGER or ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> update(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the product to update", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("id") String id,
            @RequestBody ProductUpdateDTO dto
    ) {
        service.updateProduct(UUID.fromString(id), dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List all products with pagination", description = "Endpoint to retrieve a paginated list of all products. Accessible to all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<ProductResponseDTO>> listAll(
            @Parameter(name = "page", in = ParameterIn.QUERY, description = "Page number (default: 0)", schema = @Schema(type = "integer", minimum = "0"))
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(name = "size", in = ParameterIn.QUERY, description = "Number of items per page (default: 10)", schema = @Schema(type = "integer", minimum = "1"))
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<Product> productsPage = service.listAll(page, size);
        Page<ProductResponseDTO> dtoPage = productsPage.map(mapper::toDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @Operation(summary = "Search products with optional filters", description = "Endpoint to search for products based on various criteria. Accessible to all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products matching the search criteria"),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    @GetMapping("/search")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<ProductResponseDTO>> search(
            @Parameter(name = "name", in = ParameterIn.QUERY, description = "Filter by product name")
            @RequestParam(value = "name", required = false) String name,
            @Parameter(name = "category", in = ParameterIn.QUERY, description = "Filter by category name")
            @RequestParam(value = "category", required = false) String categoryName,
            @Parameter(name = "description", in = ParameterIn.QUERY, description = "Filter by keywords in description")
            @RequestParam(value = "description", required = false) String description,
            @Parameter(name = "price", in = ParameterIn.QUERY, description = "Filter by exact price")
            @RequestParam(value = "price", required = false) BigDecimal price,
            @Parameter(name = "maxPrice", in = ParameterIn.QUERY, description = "Filter by maximum price")
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @Parameter(name = "minPrice", in = ParameterIn.QUERY, description = "Filter by minimum price")
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @Parameter(name = "stock", in = ParameterIn.QUERY, description = "Filter by minimum stock quantity")
            @RequestParam(value = "stock", required = false) Integer stock,
            @Parameter(name = "page", in = ParameterIn.QUERY, description = "Page number (default: 0)", schema = @Schema(type = "integer", minimum = "0"))
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(name = "pageSize", in = ParameterIn.QUERY, description = "Number of items per page (default: 10)", schema = @Schema(type = "integer", minimum = "1"))
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        Page<Product> products = service.search(name, categoryName, description, price, maxPrice, minPrice, stock, page, pageSize);
        Page<ProductResponseDTO> dtos = products.map(mapper::toDTO);
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Delete a product by ID", description = "Endpoint to delete a specific product based on its ID. Requires MANAGER or ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product ID format"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> delete(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the product to delete", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("id") String id
    ) {
        service.deleteById(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add a review to a product", description = "Endpoint for a user to add a review to a specific product. Requires USER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review added successfully",
                    headers = {
                            @Header(name = "Location-Review", description = "URI of the newly created review", schema = @Schema(type = "string", format = "uri")),
                            @Header(name = "Location-Product", description = "URI of the product to which the review was added", schema = @Schema(type = "string", format = "uri"))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping("/{id}/reviews")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> createReview(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the product to add the review to", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("id") String id,
            @Parameter(name = "review", in = ParameterIn.QUERY, description = "Review details", required = true)
            @RequestBody @Valid ProductReviewDTO dto) {
        ProductReview review = service.addReview(UUID.fromString(id), dto);
        URI location = generateNestedHeaderLocation(review.getProduct().getId(), "reviews", review.getId());
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Get all reviews for a product", description = "Endpoint to retrieve all reviews associated with a specific product. Accessible to all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of reviews retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product ID format"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}/reviews")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProductReviewResponseDTO>> getReviews(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the product to get reviews for", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("id") String id
    ) {
        List<ProductReviewResponseDTO> reviewsDto = service.findAllProductReviewsDTOByProductId(id);
        return ResponseEntity.ok(reviewsDto);
    }

    @Operation(summary = "Delete a specific review for a product", description = "Endpoint to delete a specific review based on its ID and the product ID. Requires USER (for own reviews), MANAGER, or ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product or review ID format"),
            @ApiResponse(responseCode = "403", description = "Forbidden (only the user who created the review, a manager, or an admin can delete it)"),
            @ApiResponse(responseCode = "404", description = "Product or review not found")
    })
    @DeleteMapping("/{id}/reviews/{reviewId}")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public ResponseEntity<Object> deleteReview(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the product containing the review", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("id") String id,
            @Parameter(name = "reviewId", in = ParameterIn.PATH, description = "ID of the review to delete", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("reviewId") String reviewId
    ) {
        service.deleteReview(id, reviewId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add an image to a product", description = "Endpoint to add a new image to a specific product. Requires MANAGER or ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Image added successfully",
                    headers = {
                            @Header(name = "Location-Image", description = "URI of the newly created image", schema = @Schema(type = "string", format = "uri")),
                            @Header(name = "Location-Product", description = "URI of the product to which the image was added", schema = @Schema(type = "string", format = "uri"))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping("/{id}/images")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> addImage(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the product to add the image to", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("id") String id,
            @RequestBody @Valid ProductImageDTO dto
    ) {
        ProductImage image = service.addImage(UUID.fromString(id), dto);
        URI location = generateNestedHeaderLocation(image.getProduct().getId(), "images", image.getId());
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Remove an image from a product", description = "Endpoint to remove a specific image from a product. Requires MANAGER or ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Image removed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product or image ID format"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Product or image not found")
    })
    @DeleteMapping("/{id}/images/{imageId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> removeImage(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the product containing the image", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("id") String id,
            @Parameter(name = "imageId", in = ParameterIn.PATH, description = "ID of the image to remove", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("imageId") String imageId
    ) {
        service.deleteImage(id, imageId);
        return ResponseEntity.noContent().build();
    }
}
