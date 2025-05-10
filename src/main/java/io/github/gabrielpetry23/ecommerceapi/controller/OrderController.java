package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.*;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.OrderMapper;
import io.github.gabrielpetry23.ecommerceapi.model.Cart;
import io.github.gabrielpetry23.ecommerceapi.model.CartItem;
import io.github.gabrielpetry23.ecommerceapi.model.Order;
import io.github.gabrielpetry23.ecommerceapi.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController implements GenericController {

    private final OrderService service;
    private final OrderMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> createOrder(@RequestBody OrderRequestDTO dto) {
        Order order = service.createOrder(dto);
        URI location = generateHeaderLocation(order.getId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Page<OrderResponseDTO>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Order> ordersPage = service.findAll(page, size);
        Page<OrderResponseDTO> dtoPage = ordersPage.map(mapper::toDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<OrderResponseDTO> getById(@PathVariable String id) {
        return service.findById(UUID.fromString(id))
                .map(order -> {
                    var dto = mapper.toDTO(order);
                    return ResponseEntity.ok(dto);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Object> updateStatus(@PathVariable String id, @RequestBody OrderStatusDTO dto) {
        service.updateStatus(UUID.fromString(id), dto);
        return ResponseEntity.noContent().build();
    }
}
