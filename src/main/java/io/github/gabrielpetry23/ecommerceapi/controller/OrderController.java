package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.*;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.OrderMapper;
import io.github.gabrielpetry23.ecommerceapi.model.Order;
import io.github.gabrielpetry23.ecommerceapi.service.OrderService;
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
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "Endpoints for managing orders")
public class OrderController implements GenericController {

    private final OrderService service;
    private final OrderMapper mapper;

    @Operation(summary = "Create a new order", description = "Endpoint to create a new order. Requires USER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully",
                    headers = @Header(name = "Location", description = "URI of the created order", schema = @Schema(type = "string", format = "uri"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> createOrder(@RequestBody OrderRequestDTO dto) {
        Order order = service.createOrder(dto);
        URI location = generateHeaderLocation(order.getId());
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "List all orders with pagination", description = "Endpoint to retrieve a paginated list of all orders. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of orders retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Page<OrderResponseDTO>> listAll(
            @Parameter(name = "page", in = ParameterIn.QUERY, description = "Page number (default: 0)", schema = @Schema(type = "integer", minimum = "0"))
            @RequestParam(defaultValue = "0") int page,
            @Parameter(name = "size", in = ParameterIn.QUERY, description = "Number of items per page (default: 10)", schema = @Schema(type = "integer", minimum = "1"))
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Order> ordersPage = service.findAll(page, size);
        Page<OrderResponseDTO> dtoPage = ordersPage.map(mapper::toDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @Operation(summary = "Get order by ID", description = "Endpoint to retrieve a specific order by its ID. Requires USER, ADMIN, or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "400", description = "Invalid order ID format"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<OrderResponseDTO> getById(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the order to retrieve", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable String id
    ) {
        return service.findById(UUID.fromString(id))
                .map(order -> {
                    var dto = mapper.toDTO(order);
                    return ResponseEntity.ok(dto);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update order status", description = "Endpoint to update the status of an order. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Object> updateStatus(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the order to update", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable String id, @RequestBody OrderStatusDTO dto
    ) {
        service.updateStatus(UUID.fromString(id), dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get order tracking information", description = "Endpoint to retrieve the tracking information for a specific order. Requires USER, ADMIN, or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tracking information retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid order ID format"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/{id}/tracking")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<TrackingResponseDTO> getTrackingInfo(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the order to retrieve tracking information for", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable String id
    ) {
        TrackingResponseDTO trackingInfo = service.getTrackingDetailsDTO(id);
        return ResponseEntity.ok(trackingInfo);
    }

    @Operation(summary = "Apply a coupon to an order", description = "Endpoint to apply a coupon to an existing order. Requires USER role and the order belongs to the current user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupon applied successfully, order total updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or coupon code"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/{id}/coupon")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponseDTO> applyCoupon(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the order to apply the coupon to", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable String id,
            @Valid @RequestBody ApplyCouponRequestDTO dto
    ) {
        Order updatedOrder = service.applyCoupon(id, dto);
        return ResponseEntity.ok(mapper.toDTO(updatedOrder));
    }
}
