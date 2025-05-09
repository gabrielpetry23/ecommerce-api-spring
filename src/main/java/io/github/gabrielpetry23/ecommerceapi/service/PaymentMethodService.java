package io.github.gabrielpetry23.ecommerceapi.service;

import aj.org.objectweb.asm.commons.Remapper;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.PaymentMethodRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.PaymentMethodResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.PaymentMethodMapper;
import io.github.gabrielpetry23.ecommerceapi.exceptions.EntityNotFoundException;
import io.github.gabrielpetry23.ecommerceapi.model.PaymentMethod;
import io.github.gabrielpetry23.ecommerceapi.model.User;
import io.github.gabrielpetry23.ecommerceapi.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
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

        paymentMethod.setCardNumber(dto.cardNumber());
        paymentMethod.setCardHolderName(dto.cardHolderName());
        paymentMethod.setExpiryDate(mapper.stringToLocalDate(dto.expiryDate()));
        paymentMethod.setCvv(dto.cvv());
        paymentMethod.setType(dto.type());
        paymentMethod.setProvider(dto.provider());
        paymentMethod.setUser(user);

        return repository.save(paymentMethod);
    }

    public List<PaymentMethod> findAllPaymentMethodesByUserId(UUID userId) {
//        return repository.findPaymentMethodByUserId(userId);
        return repository.findAllByUserId(userId);
    }

    public void updatePaymentMethod(UUID userId, UUID paymentId, PaymentMethodRequestDTO dto) {

        PaymentMethod paymentMethod = repository.findByIdAndUserId(paymentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("PaymentMethod not found"));

        if (paymentMethod == null) {
            throw new IllegalArgumentException("PaymentMethod must exist to be updated");
        }

        if (dto.cardNumber() != null) {
            paymentMethod.setCardNumber(dto.cardNumber());
        }

        if (dto.cardHolderName() != null) {
            paymentMethod.setCardHolderName(dto.cardHolderName());
        }

        if (dto.expiryDate() != null) {
            paymentMethod.setExpiryDate(mapper.stringToLocalDate(dto.expiryDate()));
        }

        if (dto.cvv() != null) {
            paymentMethod.setCvv(dto.cvv());
        }

        if (dto.type() != null) {
            paymentMethod.setType(dto.type());
        }

        if (dto.provider() != null) {
            paymentMethod.setProvider(dto.provider());
        }

        repository.save(paymentMethod);
    }

    public void delete(PaymentMethod paymentMethod) {
        repository.delete(paymentMethod);
    }

    public void deletePaymentMethod(UUID userId, UUID paymentMethodId) {
        PaymentMethod paymentMethod = repository.findByIdAndUserId(paymentMethodId, userId)
                .orElseThrow(() -> new EntityNotFoundException("PaymentMethod not found"));
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
