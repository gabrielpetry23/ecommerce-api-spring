package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.*;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.AddressMapper;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.CartMapper;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.PaymentMethodMapper;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.UserMapper;
import io.github.gabrielpetry23.ecommerceapi.exceptions.EntityNotFoundException;
import io.github.gabrielpetry23.ecommerceapi.model.*;
import io.github.gabrielpetry23.ecommerceapi.service.AddressService;
import io.github.gabrielpetry23.ecommerceapi.service.CartService;
import io.github.gabrielpetry23.ecommerceapi.service.PaymentMethodService;
import io.github.gabrielpetry23.ecommerceapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
//    GET    /users                            Listar todos os usuários                      [ADMIN, MANAGER]
//    PUT    /users/{id}                      Atualizar um usuário                          [USER (próprio), ADMIN, MANAGER]
//    DELETE /users/{id}                      Excluir um usuário                            [ADMIN, MANAGER]
//    POST   /login                           Login                                         [Público]


    @PostMapping
    @PreAuthorize("permitAll()")
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
    public ResponseEntity<Object> update(@PathVariable("id") String id, @RequestBody UserUpdateDTO dto) {
        service.update(UUID.fromString(id), dto);
        return ResponseEntity.noContent().build();

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Object> delete(@PathVariable("id") String id) {
//        return service.findById(UUID.fromString(id))
//                .map(user -> {
//                    service.delete(user);
//                    return ResponseEntity.noContent().build();
//                }).orElseGet(() -> {
//                    return ResponseEntity.notFound().build();
//                });
        service.deleteById(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    //    ENDEREÇOS
//=========
//    GET    /users/{userId}/addresses                  Obter endereços de um usuário          [USER (próprio), ADMIN, MANAGER]
//    GET    /users/{userId}/addresses/{addressId}      Obter endereço específico              [USER (próprio), ADMIN, MANAGER]
//    POST   /users/{userId}/addresses                  Criar endereço                         [USER (próprio)]
//    PUT    /users/{userId}/addresses/{addressId}      Atualizar endereço                     [USER (próprio), ADMIN, MANAGER]
//    DELETE /users/{userId}/addresses/{addressId}      Deletar endereço                       [USER (próprio), ADMIN, MANAGER]
//

    @PostMapping("/{userId}/addresses")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> createAddress(@PathVariable("userId") String userId, @RequestBody @Valid AddressDTO dto) {
        Address address = service.addAddress(UUID.fromString(userId), dto);
        URI location = generateNestedHeaderLocation(UUID.fromString(userId), "addresses", address.getId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{userId}/addresses")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<List<AddressDTO>> getAddresses(@PathVariable("userId") String userId) {
//
//        if (service.findById(UUID.fromString(userId)).isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        service.validateCurrentUserAccessOrAdmin(UUID.fromString(userId));
//
//        List<AddressDTO> addressDTOs = addressService.findAllAddressesDTOByUserId(UUID.fromString(userId));
//        return ResponseEntity.ok(addressDTOs);
        List<AddressDTO> addressDTOs = service.findAllAddressesDTOByUserId(UUID.fromString(userId));
        return ResponseEntity.ok(addressDTOs);
    }

    @GetMapping("/{userId}/addresses/{addressId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<AddressDTO> getAddress(@PathVariable("userId") String userId, @PathVariable("addressId") String addressId) {
//
//        if (service.findById(UUID.fromString(userId)).isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//        service.validateCurrentUserAccessOrAdmin(UUID.fromString(userId));
//        AddressDTO addressDTO = addressService.findAddressDTOByUserIdAndAddressId(UUID.fromString(userId), UUID.fromString(addressId));
//        return addressDTO != null ? ResponseEntity.ok(addressDTO) : ResponseEntity.notFound().build();
        AddressDTO addressDTO = service.findAddressDTOByUserIdAndId(userId, addressId);
        return ResponseEntity.ok(addressDTO);
    }

    @PutMapping("/{userId}/addresses/{addressId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<Object> updateAddress(@PathVariable("userId") String userId, @PathVariable("addressId") String addressId, @RequestBody AddressDTO dto) {

//        if (service.findById(UUID.fromString(userId)).isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        service.validateCurrentUserAccessOrAdmin(UUID.fromString(userId));
//        Optional<Address> address = addressService.findAddressByUserIdAndAddressId(UUID.fromString(userId), UUID.fromString(addressId));
//
//        if (address.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }

        service.updateAddress(userId, addressId, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/addresses/{addressId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<Object> deleteAddress(@PathVariable("userId") String userId, @PathVariable("addressId") String addressId) {
//        service.validateCurrentUserAccessOrAdmin(UUID.fromString(userId));
//        addressService.deleteAddress(UUID.fromString(userId), UUID.fromString(addressId));
//        return ResponseEntity.noContent().build();
        service.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }

    //    MÉTODOS DE PAGAMENTO
//====================
//    GET    /users/{userId}/payment-methods                    Obter métodos de pagamento        [USER (próprio), ADMIN, MANAGER]
//    GET    /users/{userId}/payment-methods/{paymentMethodId}  Obter método de pagamento         [USER (próprio), ADMIN, MANAGER]
//    POST   /users/{userId}/payment-methods                    Criar método de pagamento         [USER (próprio)]
//    PUT    /users/{userId}/payment-methods/{paymentMethodId}  Atualizar método de pagamento     [USER (próprio), ADMIN, MANAGER]
//    DELETE /users/{userId}/payment-methods/{paymentMethodId}  Deletar método de pagamento       [USER (próprio), ADMIN, MANAGER]

    @PostMapping("/{userId}/payment-methods")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> createPaymentMethod(@PathVariable("userId") String userId, @RequestBody @Valid PaymentMethodRequestDTO dto) {
//        return service.findById(UUID.fromString(userId))
//                .map(user -> {
//                    PaymentMethod paymentMethod = service.addPaymentMethod(user, dto);
//                    URI location = generateNestedHeaderLocation(user.getId(), "payment-methods", paymentMethod.getId());
//                    return ResponseEntity.created(location).build();
//                }).orElseGet(() -> {
//                    return ResponseEntity.notFound().build();
//                });
        PaymentMethod paymentMethod = service.addPaymentMethod(UUID.fromString(userId), dto);
        URI location = generateNestedHeaderLocation(UUID.fromString(userId), "payment-methods", paymentMethod.getId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{userId}/payment-methods")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<List<PaymentMethodResponseDTO>> getPaymentMethods(@PathVariable("userId") String userId) {
//        if(service.findById(UUID.fromString(userId)).isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        service.validateCurrentUserAccessOrAdmin(UUID.fromString(userId));
//        List<PaymentMethodResponseDTO> paymentMethodDTOs = paymentMethodService.findAllPaymentMethodsDTOByUserId(UUID.fromString(userId));
//        return ResponseEntity.ok(paymentMethodDTOs);
        List<PaymentMethodResponseDTO> paymentMethodDTOs = service.findAllPaymentMethodsDTOByUserId(userId);
        return ResponseEntity.ok(paymentMethodDTOs);
    }

    @GetMapping("/{userId}/payment-methods/{paymentMethodId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<PaymentMethodResponseDTO> getPaymentMethod(@PathVariable("userId") String userId, @PathVariable("paymentMethodId") String paymentMethodId) {
//        service.validateCurrentUserAccessOrAdmin(UUID.fromString(userId));
//        PaymentMethodResponseDTO paymentMethodDTO = paymentMethodService.findPaymentMethodDTOByUserIdAndPaymentMethodId(UUID.fromString(userId), UUID.fromString(paymentMethodId));
//        return paymentMethodDTO != null ? ResponseEntity.ok(paymentMethodDTO) : ResponseEntity.notFound().build();
        PaymentMethodResponseDTO paymentMethodDTO = service.findPaymentMethodDTOByUserIdAndId(userId, paymentMethodId);
        return ResponseEntity.ok(paymentMethodDTO);
    }

    @PutMapping("/{userId}/payment-methods/{paymentMethodId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<Object> updatePaymentMethod(@PathVariable("userId") String userId, @PathVariable("paymentMethodId") String paymentMethodId, @RequestBody PaymentMethodRequestDTO dto) {
        service.updatePaymentMethod(userId, paymentMethodId, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/payment-methods/{paymentMethodId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<Object> deletePaymentMethod(@PathVariable("userId") String userId, @PathVariable("paymentMethodId") String paymentMethodId) {
        service.deletePaymentMethod(userId, paymentMethodId);
        return ResponseEntity.noContent().build();
    }

    //    GET    /users/{userId}/cart             Obter carrinho de um usuário                  [USER (próprio), ADMIN, MANAGER]

    @GetMapping("/{userId}/cart")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<CartResponseDTO> getCart(@PathVariable("userId") String userId) {
//        if (service.findById(UUID.fromString(userId)).isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        service.validateCurrentUserAccessOrAdmin(UUID.fromString(userId));
//        Cart cart = cartService.findByUserId(UUID.fromString(userId)).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
//        CartResponseDTO cartDTO = cartMapper.toDTO(cart);
//        return ResponseEntity.ok(cartDTO);
        CartResponseDTO cartDTO = service.findCartDTOByUserId(UUID.fromString(userId));
        return ResponseEntity.ok(cartDTO);
    }
}
