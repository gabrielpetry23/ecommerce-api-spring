package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.PaymentMethodRequestDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.PaymentMethodMapper;
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

    public Optional<PaymentMethod> findPaymentMethodByUserIdAndPaymentMethodId(UUID userId, UUID addressId) {
        return repository.findByUserIdAndId(userId, addressId);
    }

    public PaymentMethod updatePaymentMethod(PaymentMethod paymentMethod, PaymentMethodRequestDTO dto) {

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

        return repository.save(paymentMethod);
    }

    public void delete(PaymentMethod address) {
        repository.delete(address);
    }
}
