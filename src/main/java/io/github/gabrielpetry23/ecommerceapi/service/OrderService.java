package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.OrderRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.OrderResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.OrderStatusDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.OrderMapper;
import io.github.gabrielpetry23.ecommerceapi.exceptions.EntityNotFoundException;
import io.github.gabrielpetry23.ecommerceapi.exceptions.OperationNotAllowedException;
import io.github.gabrielpetry23.ecommerceapi.model.*;
import io.github.gabrielpetry23.ecommerceapi.repository.OrderRepository;
import io.github.gabrielpetry23.ecommerceapi.security.SecurityService;
import io.github.gabrielpetry23.ecommerceapi.validators.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final UserValidator userValidator;
    private final SecurityService securityService;
    private final CartService cartService;
    private final AddressService addressService;
    private final PaymentMethodService paymentMethodService;
    private final OrderMapper mapper;

    @Transactional
    public Order createOrder(OrderRequestDTO dto) {
        User currentUser = securityService.getCurrentUser();

        Cart cart = cartService.findById(currentUser.getCart().getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(currentUser);
        order.setStatus(OrderStatus.PENDING);
        order.setTotal(cart.getTotal());
        order.setItems(convertCartItemsToOrderItems(cart.getItems(), order));

        Address addressFound = addressService.findById(UUID.fromString(dto.deliveryAddressId()))
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        order.setDeliveryAddress(addressFound);

        PaymentMethod paymentMethodFound = paymentMethodService.findById(UUID.fromString(dto.paymentMethodId()))
                .orElseThrow(() -> new EntityNotFoundException("Payment method not found"));

        order.setPaymentMethod(paymentMethodFound);

        repository.save(order);

        return order;
    }

    private OrderItem convertCartItemToOrderItem(CartItem cartItem, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(cartItem.getProduct());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(cartItem.getTotal());
        order.getItems().add(orderItem);
        // orderItem.repository(save) e fazer o get items c repo
        return orderItem;
    }

    private List<OrderItem> convertCartItemsToOrderItems(List<CartItem> cartItems, Order order) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = convertCartItemToOrderItem(cartItem, order);
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    public Optional<Order> findById(UUID id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        userValidator.validateCurrentUserAccessOrAdmin(order.getUser().getId());
        return Optional.of(order);
    }

    public Page<Order> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }

    public Page<OrderResponseDTO> findAllOrdersDTOByUserId(UUID userId, Pageable pageable) {
        return repository.findAllByUserId(userId, pageable)
                .map(mapper::toDTO);
    }

    @Transactional
    public void updateStatus(UUID id, OrderStatusDTO dto) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(dto.status().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + dto.status());
        }

        validateStatusTransition(order, newStatus);

        order.setStatus(newStatus);

        repository.save(order);
    }

    private void validateStatusTransition(Order currentOrder, OrderStatus newStatus) {
        if (currentOrder.getStatus() == OrderStatus.CANCELLED) {
            throw new OperationNotAllowedException("Cannot change status from CANCELLED.");
        }

        if (currentOrder.getStatus() == OrderStatus.DELIVERED && newStatus != OrderStatus.DELIVERED) {
            throw new OperationNotAllowedException("Cannot change status after it is marked as DELIVERED.");
        }

        if (currentOrder.getStatus() == OrderStatus.PAID && newStatus == OrderStatus.PENDING) {
            throw new OperationNotAllowedException("Cannot revert order to PENDING once it is PAID.");
        }
    }
}
