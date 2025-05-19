package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.*;
import io.github.gabrielpetry23.ecommerceapi.exceptions.EntityNotFoundException;
import io.github.gabrielpetry23.ecommerceapi.model.Address;
import io.github.gabrielpetry23.ecommerceapi.model.PaymentMethod;
import io.github.gabrielpetry23.ecommerceapi.model.User;
import io.github.gabrielpetry23.ecommerceapi.repository.UserRepository;
import io.github.gabrielpetry23.ecommerceapi.validators.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final UserValidator validator;
    private final AddressService addresService;
    private final PaymentMethodService paymentMethodService;
    private final CartService cartService;
    private final OrderService orderService;

    @Transactional
    public void save(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        repository.save(user);
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Optional<User> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public void deleteById(UUID id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        repository.delete(user);
    }

    public Page<User> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }

    @Transactional
    public Address addAddress(UUID userId, AddressDTO dto) {

        User user = repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        validator.validateCurrentUserAccess(user.getId());

        Address createdAddress = addresService.createAddressForUser(user, dto);
        user.getAddresses().add(createdAddress);

        repository.save(user);
        return createdAddress;
    }

    @Transactional
    public PaymentMethod addPaymentMethod(UUID userId, PaymentMethodRequestDTO dto) {

        User user = repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        validator.validateCurrentUserAccess(user.getId());

        PaymentMethod createdPaymentMethod = paymentMethodService.createPaymentMethodForUser(user, dto);

        user.getPaymentMethods().add(createdPaymentMethod);
        repository.save(user);
        return createdPaymentMethod;
    }

    @Transactional
    public void update(UUID userId, UserUpdateDTO dto) {
        Optional<User> userOptional = findById(userId);

        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }

        User user = userOptional.get();

        if (dto.name() != null) {
            user.setName(dto.name());
        }

        if (dto.email() != null) {
            user.setEmail(dto.email());
        }

        if (dto.password() != null) {
            user.setPassword(encoder.encode(dto.password()));
        }

        repository.save(user);
    }

    public List<AddressDTO> findAllAddressesDTOByUserId(UUID id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        validator.validateCurrentUserAccessOrAdmin(user.getId());

        return addresService.findAllAddressesDTOByUserId(user.getId());
    }

    public CartResponseDTO findCartDTOByUserId(UUID id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        validator.validateCurrentUserAccessOrAdmin(user.getId());

        return cartService.findCartDTOByUserId(id);
    }

    @Transactional
    public void deletePaymentMethod(String userId, String paymentMethodId) {
        User user = repository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        validator.validateCurrentUserAccessOrAdmin(user.getId());

        PaymentMethod paymentMethod = paymentMethodService.findByIdAndUserId(UUID.fromString(paymentMethodId), user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Payment method not found"));

        user.getPaymentMethods().remove(paymentMethod);
        repository.save(user);
        paymentMethodService.delete(paymentMethod);
    }

    @Transactional
    public void deleteAddress(String userId, String addressId) {
        User user = repository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        validator.validateCurrentUserAccessOrAdmin(user.getId());

        Address address = addresService.findAddressByUserIdAndId(UUID.fromString(userId), UUID.fromString(addressId))
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        user.getAddresses().remove(address);
        repository.save(user);
        addresService.delete(address);
    }

    @Transactional
    public void updatePaymentMethod(String userId, String paymentMethodId, PaymentMethodRequestDTO dto) {
        User user = repository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        validator.validateCurrentUserAccessOrAdmin(user.getId());

        paymentMethodService.updatePaymentMethod(UUID.fromString(userId), UUID.fromString(paymentMethodId), dto);

        repository.save(user);
    }

    @Transactional
    public void updateAddress(String userId, String addressId, AddressDTO dto) {
        User user = repository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        validator.validateCurrentUserAccessOrAdmin(user.getId());

        addresService.updateAddress(UUID.fromString(userId), UUID.fromString(addressId), dto);

        repository.save(user);
    }

    public List<PaymentMethodResponseDTO> findAllPaymentMethodsDTOByUserId(String userId) {
        User user = repository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        validator.validateCurrentUserAccessOrAdmin(user.getId());

        return paymentMethodService.findAllPaymentMethodsDTOByUserId(UUID.fromString(userId));
    }

    public PaymentMethodResponseDTO findPaymentMethodDTOByUserIdAndId(String userId, String paymentMethodId) {
        User user = repository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        validator.validateCurrentUserAccessOrAdmin(user.getId());

        return paymentMethodService.findPaymentMethodDTOByUserIdAndId(UUID.fromString(userId), UUID.fromString(paymentMethodId));
    }

    public AddressDTO findAddressDTOByUserIdAndId(String userId, String addressId) {
        User user = repository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        validator.validateCurrentUserAccessOrAdmin(user.getId());

        return addresService.findAddressDTOByUserIdAndId(UUID.fromString(userId), UUID.fromString(addressId));
    }

    public Page<OrderResponseDTO> findAllOrdersDTOByUserId(String userId, int page, int size) {
        User user = repository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        validator.validateCurrentUserAccessOrAdmin(user.getId());

        Pageable pageable = PageRequest.of(page, size);
        return orderService.findAllOrdersDTOByUserId(UUID.fromString(userId), pageable);
    }
}
