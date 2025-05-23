package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.ClientDTO;
import io.github.gabrielpetry23.ecommerceapi.controller.mappers.ClientMapper;
import io.github.gabrielpetry23.ecommerceapi.model.Client;
import io.github.gabrielpetry23.ecommerceapi.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Tag(name = "Client", description = "Endpoints for managing clients")
public class ClientController implements GenericController {

    private final ClientService service;
    private final ClientMapper mapper;

    @Operation(summary = "Create a new client", description = "Endpoint to create a new client. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client created successfully",
                    headers = @Header(name = "Location", description = "URI of the created client", schema = @Schema(type = "string", format = "uri"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Object> save(@RequestBody @Valid ClientDTO dto) {
        Client client = mapper.toEntity(dto);
        Client savedClient = service.save(client);
        URI location = generateHeaderLocation(savedClient.getId());
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Get client by ID", description = "Endpoint to retrieve a specific client by its ID. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client found"),
            @ApiResponse(responseCode = "400", description = "Invalid client ID format"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ClientDTO> findById(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the client to retrieve", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("id") String id
    ) {
        return service.findById(UUID.fromString(id))
                .map(client -> {
                    var dto = mapper.toDTO(client);
                    return ResponseEntity.ok(dto);
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update a client", description = "Endpoint to update the details of an existing client. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Client updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Object> update(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the client to update", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("id") String id,
            @RequestBody ClientDTO dto
    ) {
        service.update(id, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete a client", description = "Endpoint to delete a specific client by its ID. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Client deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid client ID format"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Object> delete(
            @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the client to delete", required = true, schema = @Schema(type = "string", format = "uuid"))
            @PathVariable("id") String id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
