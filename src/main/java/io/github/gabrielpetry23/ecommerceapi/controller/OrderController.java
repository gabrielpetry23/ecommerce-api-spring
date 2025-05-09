package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.OrderRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.OrderMapper;
import io.github.gabrielpetry23.ecommerceapi.model.Order;
import io.github.gabrielpetry23.ecommerceapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController implements GenericController{

    private final OrderService service;
//    private final OrderMapper orderMapper;

//    PEDIDOS
//=======
//    POST   /orders                          Criar novo pedido a partir de um carrinho      [USER]
//    GET    /orders                          Listar todos os pedidos                        [ADMIN, MANAGER]
//    GET    /orders/{id}                     Obter um pedido específico                     [USER (próprio), ADMIN, MANAGER]
//    GET    /users/{userId}/orders           Obter pedidos de um usuário                    [USER (próprio), ADMIN, MANAGER]
//    PUT    /orders/{id}/status              Atualizar status de um pedido                  [ADMIN, MANAGER]
//    POST   /orders/{id}/items               Adicionar item ao pedido                       [ADMIN, MANAGER]
//    GET    /orders/{orderId}/items          Obter itens de um pedido                       [USER (próprio), ADMIN, MANAGER]

    @PostMapping
    public ResponseEntity<Object> createOrder(@RequestBody OrderRequestDTO dto) {
        Order order = service.createOrder(dto);
        URI location = generateHeaderLocation(order.getId());
        return ResponseEntity.created(location).build();
    }
}
