package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController implements GenericController {

    private final CartService service;

//    CARRINHOS
//=========
//    GET    /carts/{id}                      Obter um carrinho específico                  [USER (próprio), ADMIN, MANAGER]
//    POST   /carts                           Criar um novo carrinho                        [USER]
//    POST   /carts/{id}/items                Adicionar item ao carrinho                    [USER (dono)]
//    PUT    /carts/{id}/items/{itemId}       Atualizar item no carrinho                    [USER (dono)]
//    DELETE /carts/{id}/items/{itemId}       Remover item do carrinho                      [USER (dono)]
//    DELETE /carts/{id}                      Esvaziar/Excluir o carrinho                   [USER (próprio), ADMIN, MANAGER]
//    GET    /users/{userId}/cart             Obter carrinho de um usuário                  [USER (próprio), ADMIN, MANAGER]

}
