package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.OrderRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.exceptions.EntityNotFoundException;
import io.github.gabrielpetry23.ecommerceapi.model.*;
import io.github.gabrielpetry23.ecommerceapi.repository.OrderRepository;
import io.github.gabrielpetry23.ecommerceapi.security.SecurityService;
import io.github.gabrielpetry23.ecommerceapi.validators.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

        if (dto.deliveryAddressId().isBlank()) {
            Address addressUser = currentUser.getAddresses().getFirst();
            order.setDeliveryAddress(addressUser);
        }

        if (dto.paymentMethodId().isBlank()) {
            PaymentMethod paymentMethodUser = currentUser.getPaymentMethods().getFirst();
            order.setPaymentMethod(paymentMethodUser);
        }

        Address addressFound = addressService.findById(UUID.fromString(dto.deliveryAddressId()))
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        order.setDeliveryAddress(addressFound);

        PaymentMethod paymentMethodFound = paymentMethodService.findById(UUID.fromString(dto.paymentMethodId()))
                .orElseThrow(() -> new EntityNotFoundException("Payment method not found"));

        order.setPaymentMethod(paymentMethodFound);

        return order;
    }

    private OrderItem convertCartItemToOrderItem(CartItem cartItem, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(cartItem.getProduct());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(cartItem.getTotal());
        order.getItems().add(orderItem);
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
}
