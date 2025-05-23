package io.github.gabrielpetry23.ecommerceapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.*;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.OrderMapper;
import io.github.gabrielpetry23.ecommerceapi.model.Order;
import io.github.gabrielpetry23.ecommerceapi.model.OrderStatus;
import io.github.gabrielpetry23.ecommerceapi.model.User;
import io.github.gabrielpetry23.ecommerceapi.security.SecurityService;
import io.github.gabrielpetry23.ecommerceapi.service.EmailService;
import io.github.gabrielpetry23.ecommerceapi.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private OrderMapper orderMapper;

    @MockitoBean
    private SecurityService securityService;

    @MockitoBean
    private EmailService emailService;

    private final String ORDERS_ENDPOINT = "http://localhost/orders";
    private final UUID TEST_USER_ID = UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef");
    private final UUID ANOTHER_USER_ID = UUID.fromString("fedcba98-7654-3210-fedc-ba9876543210");
    private final UUID TEST_ORDER_ID = UUID.fromString("0bc03fcc-eeb6-4e59-93de-f05b9cce6093");

    private User createMockUser(UUID id, String username, String role) {
        User user = new User();
        user.setId(id);
        user.setEmail(username);
        user.setRole(role);
        return user;
    }

    @Test
    void createOrder_ValidInput_ReturnsCreated() throws Exception {
        OrderRequestDTO requestDTO = new OrderRequestDTO(UUID.randomUUID().toString(), UUID.randomUUID().toString(), null);
        Order createdOrder = new Order();
        createdOrder.setId(UUID.randomUUID());
        User mockUser = createMockUser(TEST_USER_ID, "testUser", "USER");
        createdOrder.setUser(mockUser);

        when(orderService.createOrder(any(OrderRequestDTO.class))).thenReturn(createdOrder);

        mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .with(csrf())
                        .with(jwtForUser(TEST_USER_ID, "testUser", "USER")))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", ORDERS_ENDPOINT + "/" + createdOrder.getId()));
    }

    @Test
    void listAllOrders_AsAdmin_ReturnsOkWithPage() throws Exception {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        Order order1 = createMockOrder(UUID.randomUUID(), userId1);
        Order order2 = createMockOrder(UUID.randomUUID(), userId2);
        List<Order> orders = List.of(order1, order2);
        Page<Order> orderPage = new PageImpl<>(orders, PageRequest.of(0, 10), orders.size());
        OrderResponseDTO dto1 = createMockOrderResponseDTO(userId1, "User One");
        OrderResponseDTO dto2 = createMockOrderResponseDTO(userId2, "User Two");

        when(orderService.findAll(0, 10)).thenReturn(orderPage);
        when(orderMapper.toDTO(order1)).thenReturn(dto1);
        when(orderMapper.toDTO(order2)).thenReturn(dto2);

        mockMvc.perform(MockMvcRequestBuilders.get(ORDERS_ENDPOINT)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtForUser(UUID.randomUUID(), "adminUser", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(2));
    }

    @Test
    void getOrderById_AsOrderOwner_ReturnsOk() throws Exception {
        UUID orderId = UUID.randomUUID();
        Order order = createMockOrder(orderId, TEST_USER_ID);
        OrderResponseDTO dto = createMockOrderResponseDTO(TEST_USER_ID, "Test User");

        when(orderService.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toDTO(order)).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get(ORDERS_ENDPOINT + "/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtForUser(TEST_USER_ID, "testUser", "USER")))
                .andExpect(status().isOk());
    }

    @Test
    void getOrderById_AsAnotherUser_ReturnsForbidden() throws Exception {
        UUID orderId = UUID.fromString("14d234e5-37bb-482d-a1b9-5a7e1d7088c1");
        Order order = createMockOrder(orderId, TEST_USER_ID);

        User anotherUser = createMockUser(ANOTHER_USER_ID, "anotherUser", "USER");

        Mockito.when(securityService.getCurrentUser()).thenReturn(anotherUser);

        when(orderService.findById(orderId)).thenAnswer(invocation -> {
            if (!order.getUser().getId().equals(ANOTHER_USER_ID)) {
                throw new AccessDeniedException("Access denied.");
            }
            return Optional.of(order);
        });

        mockMvc.perform(MockMvcRequestBuilders.get(ORDERS_ENDPOINT + "/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtForUser(ANOTHER_USER_ID, "anotherUser", "USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateOrderStatus_AsManager_ReturnsNoContent() throws Exception {
        when(securityService.getCurrentUser()).thenReturn(createMockUser(UUID.randomUUID(), "managerUser", "MANAGER"));
        UUID orderId = UUID.randomUUID();
        OrderStatusDTO statusDTO = new OrderStatusDTO("PROCESSING");

        doNothing().when(orderService).updateStatus(eq(orderId), any(OrderStatusDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.put(ORDERS_ENDPOINT + "/" + orderId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDTO))
                        .with(csrf())
                        .with(jwtForUser(UUID.randomUUID(), "managerUser", "MANAGER")))
                .andExpect(status().isNoContent());
    }

    @Test
    void getTrackingInfo_AsOrderOwner_ReturnsOk() throws Exception {
        UUID orderId = TEST_ORDER_ID;

        Order order = createMockOrder(orderId, TEST_USER_ID);
        TrackingResponseDTO trackingDTO = new TrackingResponseDTO("TRACK123", "CarrierX", "In Transit", LocalDateTime.now().plusDays(2).toLocalDate());

        when(orderService.findById(orderId)).thenReturn(Optional.of(order));
        when(orderService.getTrackingDetailsDTO(orderId.toString())).thenReturn(trackingDTO);

        mockMvc.perform(MockMvcRequestBuilders.get(ORDERS_ENDPOINT + "/" + orderId + "/tracking")
                        .with(jwtForUser(TEST_USER_ID, "testUser", "USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.trackingCode").value("TRACK123"));
    }

    @Test
    void applyCoupon_AsOrderOwner_ReturnsOkWithUpdatedOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        ApplyCouponRequestDTO couponDTO = new ApplyCouponRequestDTO("SUMMER20");
        Order updatedOrder = createMockOrder(orderId, TEST_USER_ID, new BigDecimal("80.00"));
        OrderResponseDTO updatedOrderDTO = createMockOrderResponseDTO(TEST_USER_ID, "Test User", new BigDecimal("80.00"));

        when(orderService.findById(orderId)).thenReturn(Optional.of(updatedOrder));
        when(orderService.applyCoupon(eq(orderId.toString()), any(ApplyCouponRequestDTO.class))).thenReturn(updatedOrder);
        when(orderMapper.toDTO(updatedOrder)).thenReturn(updatedOrderDTO);

        mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_ENDPOINT + "/" + orderId + "/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(couponDTO))
                        .with(csrf())
                        .with(jwtForUser(TEST_USER_ID, "testUser", "USER")))
                .andExpect(status().isOk());
    }

    private Order createMockOrder(UUID id, UUID userId) {
        Order order = new Order();
        order.setId(id);
        User user = new User();
        user.setId(userId);
        order.setUser(user);
        order.setTotal(BigDecimal.TEN);
        order.setStatus(OrderStatus.PENDING);
        return order;
    }

    private Order createMockOrder(UUID id, UUID userId, BigDecimal total) {
        Order order = new Order();
        order.setId(id);
        User user = new User();
        user.setId(userId);
        order.setUser(user);
        order.setTotal(total);
        order.setStatus(OrderStatus.PENDING);
        return order;
    }

    private OrderResponseDTO createMockOrderResponseDTO(UUID userId, String userName) {
        UserNameIdDTO userDTO = new UserNameIdDTO(userId, userName);
        return new OrderResponseDTO(userDTO, Collections.emptyList(), BigDecimal.TEN, OrderStatus.PENDING);
    }

    private OrderResponseDTO createMockOrderResponseDTO(UUID userId, String userName, BigDecimal total) {
        UserNameIdDTO userDTO = new UserNameIdDTO(userId, userName);
        return new OrderResponseDTO(userDTO, Collections.emptyList(), total, OrderStatus.PENDING);
    }

    private static RequestPostProcessor jwtForUser(UUID userId, String username, String role) {
        return jwt().jwt(jwt -> jwt
                        .claim("sub", userId.toString())
                        .claim("preferred_username", username))
                .authorities(createAuthorityList(role));
    }
}