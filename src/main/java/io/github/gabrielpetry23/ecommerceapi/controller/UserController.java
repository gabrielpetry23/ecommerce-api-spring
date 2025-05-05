package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.*;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.AddressMapper;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.PaymentMethodMapper;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.UserMapper;
import io.github.gabrielpetry23.ecommerceapi.model.*;
import io.github.gabrielpetry23.ecommerceapi.service.AddressService;
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
    private final AddressMapper addressMapper;
    private final PaymentMethodMapper paymentMethodMapper;
    private final PaymentMethodService paymentMethodService;
    private final AddressService addressService;

//    USUÁRIOS
//========
//    POST   /users                           Criar um novo usuário                         [Público]
//    GET    /users/{id}                      Obter um usuário específico                   [USER (próprio), ADMIN, MANAGER]
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
                    .map(addressMapper::toEntity)
                    .toList();

            user.setAddresses(addresses);
        }

        if (dto.paymentMethods() != null) {
            List<PaymentMethod> paymentMethods = dto.paymentMethods().stream()
                    .map(paymentMethodMapper::toEntity)
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
        return service.findById(UUID.fromString(userId))
                .map(user -> {
                    service.addAddress(user, dto);
                    return ResponseEntity.ok().build();
                }).orElseGet(() -> {
                    return ResponseEntity.notFound().build();
                });
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    @GetMapping("/{userId}/addresses")
    public ResponseEntity<List<AddressDTO>> getAddresses(@PathVariable("userId") String userId) {
        service.validateCurrentUserAccessOrAdmin(UUID.fromString(userId));
        List<AddressDTO> addressDTOs = addressService.findAllAddressesByUserId(UUID.fromString(userId))
                .stream()
                .map(addressMapper::toDTO)
                .toList();
        return ResponseEntity.ok(addressDTOs);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    @GetMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddress(@PathVariable("userId") String userId, @PathVariable("addressId") String addressId) {
        service.validateCurrentUserAccessOrAdmin(UUID.fromString(userId));
        return addressService.findAddressByUserIdAndAddressId(UUID.fromString(userId), UUID.fromString(addressId))
                .map(address -> {
                    var dto = addressMapper.toDTO(address);
                    return ResponseEntity.ok(dto);
                }).orElseGet(() -> {
                    return ResponseEntity.notFound().build();
                });
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    @PutMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<Object> updateAddress(@PathVariable("userId") String userId, @PathVariable("addressId") String addressId, @RequestBody @Valid AddressResponseDTO dto) {
        service.validateCurrentUserAccessOrAdmin(UUID.fromString(userId));
        Optional<Address> address = addressService.findAddressByUserIdAndAddressId(UUID.fromString(userId), UUID.fromString(addressId));

        if (address.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        addressService.updateAddress(address.get(), dto);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    @DeleteMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<Object> deleteAddress(@PathVariable("userId") String userId, @PathVariable("addressId") String addressId) {
        service.validateCurrentUserAccessOrAdmin(UUID.fromString(userId));
        return addressService.findAddressByUserIdAndAddressId(UUID.fromString(userId), UUID.fromString(addressId))
                .map(address -> {
                    addressService.delete(address);
                    return ResponseEntity.noContent().build();
                }).orElseGet(() -> {
                    return ResponseEntity.notFound().build();
                });
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
        return service.findById(UUID.fromString(userId))
                .map(user -> {
                    service.addPaymentMethod(user, dto);
                    return ResponseEntity.ok().build();
                }).orElseGet(() -> {
                    return ResponseEntity.notFound().build();
                });
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    @GetMapping("/{userId}/payment-methods")
    public ResponseEntity<List<PaymentMethodResponseDTO>> getPaymentMethods(@PathVariable("userId") String userId) {
        service.validateCurrentUserAccessOrAdmin(UUID.fromString(userId));
        List<PaymentMethodResponseDTO> paymentMethodDTOs = paymentMethodService.findAllPaymentMethodesByUserId(UUID.fromString(userId))
                .stream()
                .map(paymentMethodMapper::toDTO)
                .toList();
        return ResponseEntity.ok(paymentMethodDTOs);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    @GetMapping("/{userId}/payment-methods/{paymentMethodId}")
    public ResponseEntity<PaymentMethodResponseDTO> getPaymentMethod(@PathVariable("userId") String userId, @PathVariable("paymentMethodId") String paymentMethodId) {
        service.validateCurrentUserAccessOrAdmin(UUID.fromString(userId));
        return paymentMethodService.findPaymentMethodByUserIdAndPaymentMethodId(UUID.fromString(userId), UUID.fromString(paymentMethodId))
                .map(paymentMethod -> {
                    var dto = paymentMethodMapper.toDTO(paymentMethod);
                    return ResponseEntity.ok(dto);
                }).orElseGet(() -> {
                    return ResponseEntity.notFound().build();
                });
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    @PutMapping("/{userId}/payment-methods/{paymentMethodId}")
    public ResponseEntity<Object> updatePaymentMethod(@PathVariable("userId") String userId, @PathVariable("paymentMethodId") String paymentMethodId, @RequestBody PaymentMethodRequestDTO dto) {
        service.validateCurrentUserAccessOrAdmin(UUID.fromString(userId));
        Optional<PaymentMethod> paymentMethod = paymentMethodService.findPaymentMethodByUserIdAndPaymentMethodId(UUID.fromString(userId), UUID.fromString(paymentMethodId));

        if (paymentMethod.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        paymentMethodService.updatePaymentMethod(paymentMethod.get(), dto);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    @DeleteMapping("/{userId}/payment-methods/{paymentMethodId}")
    public ResponseEntity<Object> deletePaymentMethod(@PathVariable("userId") String userId, @PathVariable("paymentMethodId") String paymentMethodId) {
        service.validateCurrentUserAccessOrAdmin(UUID.fromString(userId));
        return paymentMethodService.findPaymentMethodByUserIdAndPaymentMethodId(UUID.fromString(userId), UUID.fromString(paymentMethodId))
                .map(paymentMethod -> {
                    paymentMethodService.delete(paymentMethod);
                    return ResponseEntity.noContent().build();
                }).orElseGet(() -> {
                    return ResponseEntity.notFound().build();
                });
    }
}
