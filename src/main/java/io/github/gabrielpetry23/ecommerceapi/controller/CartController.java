package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.CartItemRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.CartResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.PaymentMethodRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.UserDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.CartMapper;
import io.github.gabrielpetry23.ecommerceapi.model.Cart;
import io.github.gabrielpetry23.ecommerceapi.model.CartItem;
import io.github.gabrielpetry23.ecommerceapi.model.PaymentMethod;
import io.github.gabrielpetry23.ecommerceapi.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController implements GenericController {

    private final CartService service;
    private final CartMapper mapper;

//    CARRINHOS
//=========
//    GET    /carts/{id}                      Obter um carrinho específico                  [USER (próprio), ADMIN, MANAGER]
//    POST   /carts                           Criar um novo carrinho                        [USER]
//    POST   /carts/{id}/items                Adicionar item ao carrinho                    [USER (dono)]
//    PUT    /carts/{id}/items/{itemId}       Atualizar quantidade do item no carrinho      [USER (dono)]
//    DELETE /carts/{id}/items/{itemId}       Remover item do carrinho                      [USER (dono)]
//    DELETE /carts/{id}                      Esvaziar/Excluir o carrinho                   [USER (próprio), ADMIN, MANAGER]

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> createCart() {
        Cart cart = service.createCart();
        URI location = generateHeaderLocation(cart.getId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<CartResponseDTO> getById(@PathVariable String id) {
        service.validateCartOwnerIsCurrentUserOrAdminOrManager(UUID.fromString(id));
        return service.findById(UUID.fromString(id))
                .map(cart -> {
                    var dto = mapper.toDTO(cart);
                    return ResponseEntity.ok(dto);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/items")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> addItem(@PathVariable("id") String cartId, @RequestBody @Valid CartItemRequestDTO dto) {
        CartItem item = service.addItem(UUID.fromString(cartId), dto);
        URI location = generateNestedHeaderLocation(item.getCart().getId(), "items", item.getId());
        return ResponseEntity.created(location).build();
    }

    public ResponseEntity<Object> updateItemQuantity(@PathVariable("cartId") String cartId, @PathVariable("itemId") String itemId, @RequestBody CartItemRequestDTO dto) {
        service.updateItemQuantity(UUID.fromString(cartId), UUID.fromString(itemId), dto);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCart(@PathVariable String id) {
        service.validateCartOwnerIsCurrentUserOrAdminOrManager(UUID.fromString(id));
        service.deleteById(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable("cartId") String cartId, @PathVariable("itemId") String itemId) {
        service.deleteItem(UUID.fromString(cartId), UUID.fromString(itemId));
        return ResponseEntity.noContent().build();
    }
}
