package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.CartItemRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.CartResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.CartMapper;
import io.github.gabrielpetry23.ecommerceapi.model.Cart;
import io.github.gabrielpetry23.ecommerceapi.model.CartItem;
import io.github.gabrielpetry23.ecommerceapi.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Endpoints for managing shopping carts")
public class CartController implements GenericController {

    private final CartService service;
    private final CartMapper mapper;

    @Operation(summary = "Create a new cart", description = "Endpoint to create a new shopping cart. Requires USER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cart created successfully",
                    headers = @Header(name = "Location", description = "URI of the created cart", schema = @Schema(type = "string", format = "uri"))),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> createCart() {
        Cart cart = service.createCart();
        URI location = generateHeaderLocation(cart.getId());
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Get cart by ID", description = "Endpoint to retrieve a specific cart by its ID. Requires USER, ADMIN, or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart found"),
            @ApiResponse(responseCode = "400", description = "Invalid cart ID format"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<CartResponseDTO> getById(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the cart to retrieve", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable String id
    ) {
        return service.findById(UUID.fromString(id))
                .map(cart -> {
                    var dto = mapper.toDTO(cart);
                    return ResponseEntity.ok(dto);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Add item to cart", description = "Endpoint to add an item to a specific cart. Requires USER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item added successfully",
                    headers = @Header(name = "Location", description = "URI of the added item", schema = @Schema(type = "string", format = "uri"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    @PostMapping("/{id}/items")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> addItem(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the cart to add the item to", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("id") String cartId,
            @RequestBody @Valid CartItemRequestDTO dto
    ) {
        CartItem item = service.addItem(UUID.fromString(cartId), dto);
        URI location = generateNestedHeaderLocation(item.getCart().getId(), "items", item.getId());
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Update item quantity", description = "Endpoint to update the quantity of an item in a specific cart. Requires USER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item quantity updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Cart or item not found")
    })
    @PutMapping("/{cartId}/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> updateItemQuantity(
            @Parameter(name = "cartId", in = ParameterIn.PATH, description = "ID of the cart", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("cartId") String cartId,
            @Parameter(name = "itemId", in = ParameterIn.PATH, description = "ID of the item to update", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("itemId") String itemId,
            @RequestBody CartItemRequestDTO dto
    ) {
        service.updateItemQuantity(UUID.fromString(cartId), UUID.fromString(itemId), dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Empty a cart", description = "Endpoint to empty a specific cart. Requires USER, ADMIN, or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cart emptied successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid cart ID format"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<Object> emptyCart(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the cart to empty", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable String id
    ) {
        service.emptyCart(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete an item from cart", description = "Endpoint to delete a specific item from a cart. Requires USER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid cart or item ID format"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Cart or item not found")
    })
    @DeleteMapping("/{cartId}/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> deleteItem(
            @Parameter(name = "cartId", in = ParameterIn.PATH, description = "ID of the cart", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("cartId") String cartId,
            @Parameter(name = "itemId", in = ParameterIn.PATH, description = "ID of the item to delete", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("itemId") String itemId
    ) {
        service.deleteItem(UUID.fromString(cartId), UUID.fromString(itemId));
        return ResponseEntity.noContent().build();
    }
}
