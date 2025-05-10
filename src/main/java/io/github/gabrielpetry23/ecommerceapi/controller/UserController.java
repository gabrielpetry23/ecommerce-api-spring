package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.*;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.UserMapper;
import io.github.gabrielpetry23.ecommerceapi.model.*;
import io.github.gabrielpetry23.ecommerceapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController implements GenericController {

    private final UserService service;
    private final UserMapper mapper;

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
    public ResponseEntity<Page<UserDetailsDTO>> getAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<User> usersPage = service.findAll(page, size);
        Page<UserDetailsDTO> dtos = usersPage.map(mapper::toDTO);
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
        service.deleteById(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

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
        List<AddressDTO> addressDTOs = service.findAllAddressesDTOByUserId(UUID.fromString(userId));
        return ResponseEntity.ok(addressDTOs);
    }

    @GetMapping("/{userId}/addresses/{addressId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<AddressDTO> getAddress(@PathVariable("userId") String userId, @PathVariable("addressId") String addressId) {
        AddressDTO addressDTO = service.findAddressDTOByUserIdAndId(userId, addressId);
        return ResponseEntity.ok(addressDTO);
    }

    @PutMapping("/{userId}/addresses/{addressId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<Object> updateAddress(@PathVariable("userId") String userId, @PathVariable("addressId") String addressId, @RequestBody AddressDTO dto) {
        service.updateAddress(userId, addressId, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/addresses/{addressId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<Object> deleteAddress(@PathVariable("userId") String userId, @PathVariable("addressId") String addressId) {
        service.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/payment-methods")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> createPaymentMethod(@PathVariable("userId") String userId, @RequestBody @Valid PaymentMethodRequestDTO dto) {
        PaymentMethod paymentMethod = service.addPaymentMethod(UUID.fromString(userId), dto);
        URI location = generateNestedHeaderLocation(UUID.fromString(userId), "payment-methods", paymentMethod.getId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{userId}/payment-methods")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<List<PaymentMethodResponseDTO>> getPaymentMethods(@PathVariable("userId") String userId) {
        List<PaymentMethodResponseDTO> paymentMethodDTOs = service.findAllPaymentMethodsDTOByUserId(userId);
        return ResponseEntity.ok(paymentMethodDTOs);
    }

    @GetMapping("/{userId}/payment-methods/{paymentMethodId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<PaymentMethodResponseDTO> getPaymentMethod(@PathVariable("userId") String userId, @PathVariable("paymentMethodId") String paymentMethodId) {
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

    @GetMapping("/{userId}/cart")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<CartResponseDTO> getCart(@PathVariable("userId") String userId) {
        CartResponseDTO cartDTO = service.findCartDTOByUserId(UUID.fromString(userId));
        return ResponseEntity.ok(cartDTO);
    }

    @GetMapping("/{userId}/orders")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<Page<OrderResponseDTO>> getOrders(
            @PathVariable("userId") String userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<OrderResponseDTO> dtoPage = service.findAllOrdersDTOByUserId(userId, page, size);
        return ResponseEntity.ok(dtoPage);
    }
}
