package io.github.gabrielpetry23.ecommerceapi.controller;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

public interface GenericController {
    default URI generateHeaderLocation(UUID id) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }

    default URI generateNestedHeaderLocation(UUID parentId, String nestedPath, UUID childId) {
        return ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{parentId}/" + nestedPath + "/{childId}")
                .buildAndExpand(parentId, childId)
                .toUri();
    }
}
