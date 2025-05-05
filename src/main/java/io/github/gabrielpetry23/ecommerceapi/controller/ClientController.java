package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.ClientDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.ClientMapper;
import io.github.gabrielpetry23.ecommerceapi.model.Client;
import io.github.gabrielpetry23.ecommerceapi.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Slf4j
public class ClientController {

//    CLIENTES (OAuth)
//================
//    POST   /clients                         Criar um novo Client                           [ADMIN, MANAGER]
//    GET    /clients/{id}                    Obter um Client específico                     [ADMIN, MANAGER]
//    PUT    /clients/{id}                    Atualizar um Client                            [ADMIN, MANAGER]
//    DELETE /clients/{id}                    Excluir um Client                              [ADMIN, MANAGER]

    private final ClientService service;
    private final ClientMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Object> save(@RequestBody @Valid ClientDTO dto) {
        var client = mapper.toEntity(dto);
        service.save(client);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ClientDTO> findById(@PathVariable("id") String id) {
        return service.findById(UUID.fromString(id))
                .map(client -> {
                    var dto = mapper.toDTO(client);
                    return ResponseEntity.ok(dto);
                }).orElseGet(() -> {
                    return ResponseEntity.notFound().build();
                });
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Object> update(@PathVariable("id") String id, @RequestBody ClientDTO dto) {
        service.update(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Object> delete(@PathVariable("id") String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
