package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartsService {

    private final CartRepository repository;
}
