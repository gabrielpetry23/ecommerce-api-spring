package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.CartItemRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.CartResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.CartMapper;
import io.github.gabrielpetry23.ecommerceapi.exceptions.EntityNotFoundException;
import io.github.gabrielpetry23.ecommerceapi.model.Cart;
import io.github.gabrielpetry23.ecommerceapi.model.CartItem;
import io.github.gabrielpetry23.ecommerceapi.repository.CartRepository;
import io.github.gabrielpetry23.ecommerceapi.security.SecurityService;
import io.github.gabrielpetry23.ecommerceapi.validators.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository repository;
    private final SecurityService securityService;
    private final UserValidator validator;
    private final CartItemService cartItemService;
    private CartMapper mapper;

    @Transactional
    public Cart createCart() {

        if (repository.findByUserId(securityService.getCurrentUser().getId()).isPresent()) {
            throw new IllegalArgumentException("User already has a cart.");
        }

        Cart cart = new Cart();
        cart.setUser(securityService.getCurrentUser());
        return repository.save(cart);
    }

    public void validateCartOwnerIsCurrentUser(UUID id) {
        Cart cart = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        if (!cart.getUser().getId().equals(securityService.getCurrentUser().getId())) {
            throw new AccessDeniedException("Access denied.");
        }
    }

    public Optional<Cart> findById(UUID id) {
        Cart cart = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        validator.validateCurrentUserAccessOrAdmin(cart.getUser().getId());
        return Optional.of(cart);
    }

    @Transactional
    public CartItem addItem(UUID cartId, CartItemRequestDTO dto) {
        Cart cart = repository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        validator.validateCurrentUserAccess(cart.getUser().getId());

        CartItem cartItem = cartItemService.createCartItem(cart, dto);
        cart.getItems().add(cartItem);
        cart.setTotal(calculateTotalPrice(cart));
        repository.save(cart);
        return cartItem;
    }

    @Transactional
    public void updateItemQuantity(UUID cartId, UUID itemId, CartItemRequestDTO dto) {
        CartItem cartItem = cartItemService.findByIdAndCartId(itemId, cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        validator.validateCurrentUserAccess(cartItem.getCart().getUser().getId());

        cartItemService.updateCartItemQuantity(cartItem, dto);

        Cart cart = repository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        cart.setTotal(calculateTotalPrice(cart));
        repository.save(cart);
    }

    @Transactional
    public void emptyCart(UUID id) {
        Cart cart = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        validator.validateCurrentUserAccessOrAdmin(cart.getUser().getId());
        cart.getItems().clear();
        repository.save(cart);
    }

    @Transactional
    public void deleteItem(UUID cartId, UUID itemId) {
        Cart cart = repository.findById(cartId).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        validator.validateCurrentUserAccess(cart.getUser().getId());

        CartItem cartItem = cartItemService.findByIdAndCartId(itemId, cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        cart.getItems().remove(cartItem);
        repository.save(cart);
        cartItemService.delete(cartItem);
    }

    public CartResponseDTO findCartDTOByUserId(UUID id) {
        Cart cart = repository.findByUserId(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        return mapper.toDTO(cart);
    }

    private BigDecimal calculateTotalPrice(Cart cart) {
        return cart.getItems().stream()
                .map(CartItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
