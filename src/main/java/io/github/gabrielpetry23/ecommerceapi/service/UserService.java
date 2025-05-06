package io.github.gabrielpetry23.ecommerceapi.service;

import aj.org.objectweb.asm.commons.Remapper;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.AddressDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.PaymentMethodRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.UserUpdateDTO;
import io.github.gabrielpetry23.ecommerceapi.model.Address;
import io.github.gabrielpetry23.ecommerceapi.model.PaymentMethod;
import io.github.gabrielpetry23.ecommerceapi.model.User;
import io.github.gabrielpetry23.ecommerceapi.repository.AddressRepository;
import io.github.gabrielpetry23.ecommerceapi.repository.UserRepository;
import io.github.gabrielpetry23.ecommerceapi.security.SecurityService;
import io.github.gabrielpetry23.ecommerceapi.validators.UserValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final SecurityService securityService;
    private final UserValidator validator;
    private final AddressService addresService;
    private final PaymentMethodService paymentMethodService;

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

    public void delete(User user) {
        repository.delete(user);
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public Address addAddress(User user, AddressDTO dto) {

        if (user.getId() == null) {
            throw new IllegalArgumentException("User must exist to add an address");
        }

        validator.validateCurrentUserAccess(user.getId());

        Address createdAddress = addresService.createAddressForUser(user, dto);
        user.getAddresses().add(createdAddress);

        repository.save(user);
        return createdAddress;
    }

    public void validateCurrentUserAccessOrAdmin(UUID userId) {
        validator.validateCurrentUserAccessOrAdmin(userId);
    }

    public PaymentMethod addPaymentMethod(User user, PaymentMethodRequestDTO dto) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("User must exist to add a payment method");
        }

        validator.validateCurrentUserAccess(user.getId());

        PaymentMethod createdPaymentMethod = paymentMethodService.createPaymentMethodForUser(user, dto);

        user.getPaymentMethods().add(createdPaymentMethod);
        repository.save(user);
        return createdPaymentMethod;
    }

    public boolean existsById(UUID userId) {
        return repository.existsById(userId);
    }

    public void update(UUID userId, UserUpdateDTO dto) {
        Optional<User> userOptional = findById(userId);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOptional.get();

        if (dto.name() != null) {
            user.setName(dto.name());
        }

        if (dto.email() != null) {
            user.setEmail(dto.email());
        }

        if (dto.password() != null) {
            user.setPassword(dto.password());
        }

        repository.save(user);
    }
}
