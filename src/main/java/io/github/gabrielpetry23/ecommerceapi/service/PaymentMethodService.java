package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.PaymentMethodRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.PaymentMethodResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.PaymentMethodUpdateDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.PaymentMethodMapper;
import io.github.gabrielpetry23.ecommerceapi.exceptions.EntityNotFoundException;
import io.github.gabrielpetry23.ecommerceapi.model.PaymentMethod;
import io.github.gabrielpetry23.ecommerceapi.model.User;
import io.github.gabrielpetry23.ecommerceapi.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository repository;
    private final PaymentMethodMapper mapper;

    public PaymentMethod createPaymentMethodForUser(User user, PaymentMethodRequestDTO dto) {

        PaymentMethod paymentMethod = new PaymentMethod();

        if (dto.cardBrand() != null) {
            paymentMethod.setCardBrand(dto.cardBrand());
        }

        if (dto.last4Digits() != null) {
            paymentMethod.setLast4Digits(dto.last4Digits());
        }
        paymentMethod.setPaymentToken(dto.paymentToken());
        paymentMethod.setType(dto.type());
        paymentMethod.setProvider(dto.provider());
        paymentMethod.setUser(user);

        return repository.save(paymentMethod);
    }

    public void updatePaymentMethod(UUID userId, UUID paymentId, PaymentMethodUpdateDTO dto) {

        PaymentMethod paymentMethod = repository.findByIdAndUserId(paymentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("PaymentMethod not found"));

        if (paymentMethod == null) {
            throw new IllegalArgumentException("PaymentMethod must exist to be updated");
        }

        if (dto.type() != null && !dto.type().isBlank()) {
            paymentMethod.setType(dto.type());
        }
        if (dto.provider() != null && !dto.provider().isBlank()) {
            paymentMethod.setProvider(dto.provider());
        }

        if (dto.cardBrand() != null && !dto.cardBrand().isBlank()) {
            paymentMethod.setCardBrand(dto.cardBrand());
        }

        repository.save(paymentMethod);
    }

    public void delete(PaymentMethod paymentMethod) {
        repository.delete(paymentMethod);
    }

    public List<PaymentMethodResponseDTO> findAllPaymentMethodsDTOByUserId(UUID userId) {
        return repository.findAllByUserId(userId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public PaymentMethodResponseDTO findPaymentMethodDTOByUserIdAndId(UUID userId, UUID id) {
        return repository.findByIdAndUserId(id, userId)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("PaymentMethod not found"));
    }

    public Optional<PaymentMethod> findByIdAndUserId(UUID paymentId, UUID userId) {
        return repository.findByIdAndUserId(paymentId, userId);
    }

    public Optional<PaymentMethod> findById(UUID id) {
        return repository.findById(id);
    }
}
