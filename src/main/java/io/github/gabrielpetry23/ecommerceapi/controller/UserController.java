package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.ProductRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.UserDetailsDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.UserUpdateDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.UserMapper;
import io.github.gabrielpetry23.ecommerceapi.exceptions.ResourceNotFoundException;
import io.github.gabrielpetry23.ecommerceapi.model.*;
import io.github.gabrielpetry23.ecommerceapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.UserDTO;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController implements GenericController{

    private final UserService service;
    private final UserMapper mapper;

//    USUÁRIOS
//========
//    POST   /users                           Criar um novo usuário                         [Público]
//    GET    /users/{id}                      Obter um usuário específico                   [USER (próprio), ADMIN, MANAGER]
//    PUT    /users/{id}                      Atualizar um usuário                          [USER (próprio), ADMIN, MANAGER]
//    DELETE /users/{id}                      Excluir um usuário                            [ADMIN, MANAGER]
//    POST   /login                           Login                                         [Público]

//    ENDEREÇOS
//=========
//    GET    /users/{userId}/addresses                  Obter endereços de um usuário          [USER (próprio), ADMIN, MANAGER]
//    GET    /users/{userId}/addresses/{addressId}      Obter endereço específico              [USER (próprio), ADMIN, MANAGER]
//    POST   /users/{userId}/addresses                  Criar endereço                         [USER (próprio)]
//    PUT    /users/{userId}/addresses/{addressId}      Atualizar endereço                     [USER (próprio), ADMIN, MANAGER]
//    DELETE /users/{userId}/addresses/{addressId}      Deletar endereço                       [USER (próprio), ADMIN, MANAGER]
//
//    MÉTODOS DE PAGAMENTO
//====================
//    GET    /users/{userId}/payment-methods                    Obter métodos de pagamento        [USER (próprio), ADMIN, MANAGER]
//    GET    /users/{userId}/payment-methods/{paymentMethodId}  Obter método de pagamento         [USER (próprio), ADMIN, MANAGER]
//    POST   /users/{userId}/payment-methods                    Criar método de pagamento         [USER (próprio)]
//    PUT    /users/{userId}/payment-methods/{paymentMethodId}  Atualizar método de pagamento     [USER (próprio), ADMIN, MANAGER]
//    DELETE /users/{userId}/payment-methods/{paymentMethodId}  Deletar método de pagamento       [USER (próprio), ADMIN, MANAGER]

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> create(@RequestBody @Valid UserDTO dto) {
        var user = mapper.toEntity(dto);
        service.save(user);
        URI location = generateHeaderLocation(user.getId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<UserDetailsDTO> getById(@PathVariable("id") String id) {
        return service.findById(UUID.fromString(id))
                .map(user -> {
                    var dto = mapper.toDTO(user);
                    return ResponseEntity.ok(dto);
                }).orElseGet(() -> {
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<UserDetailsDTO>> getAll() {
        List<User> users = service.findAll();
        List<UserDetailsDTO> dtos = users.stream()
                .map(mapper::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> update(@PathVariable("id") String id, @RequestBody @Valid UserUpdateDTO dto) {
        Optional<User> userOptional = service.findById(UUID.fromString(id));

        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var user = userOptional.get();

        if (dto.name() != null) {
            user.setName(dto.name());
        }

        if (dto.email() != null) {
            user.setEmail(dto.email());
        }

        if (dto.password() != null) {
            user.setPassword(dto.password());
        }

        if (dto.role() != null) {
            user.setRole(dto.role());
        }

        if (dto.addresses() != null) {
            List<Address> addresses = dto.addresses().stream()
                    .map(mapper::toEntity)
                    .toList();

            user.setAddresses(addresses);
        }

        if (dto.paymentMethods() != null) {
            List<PaymentMethod> paymentMethods = dto.paymentMethods().stream()
                    .map(mapper::toEntity)
                    .toList();

            user.setPaymentMethods(paymentMethods);
        }

        service.update(user);

        return ResponseEntity.noContent().build();

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> delete(@PathVariable("id") String id) {
        return service.findById(UUID.fromString(id))
                .map(user -> {
                    service.delete(user);
                    return ResponseEntity.noContent().build();
                }).orElseGet(() -> {
                    return ResponseEntity.notFound().build();
                });
    }


}
