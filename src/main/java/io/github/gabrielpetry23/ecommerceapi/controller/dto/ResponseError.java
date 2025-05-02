package io.github.gabrielpetry23.ecommerceapi.controller.dto;

import  org.springframework.http.HttpStatus;

import java.util.List;

public record ResponseError(int status, String message, List<CustomFieldError> errors) {

    public static ResponseError defaultResponse(String msg) {
        return new ResponseError(HttpStatus.BAD_REQUEST.value(), msg, List.of());
    }

    public static ResponseError conflict(String msg) {
        return new ResponseError(HttpStatus.CONFLICT.value(), msg, List.of());
    }


}
