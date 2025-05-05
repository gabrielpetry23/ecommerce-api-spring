package io.github.gabrielpetry23.ecommerceapi.controller.mappers;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.ClientDTO;
import io.github.gabrielpetry23.ecommerceapi.model.Client;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    Client toEntity(ClientDTO clientDTO);

    ClientDTO toDTO(Client client);
}
