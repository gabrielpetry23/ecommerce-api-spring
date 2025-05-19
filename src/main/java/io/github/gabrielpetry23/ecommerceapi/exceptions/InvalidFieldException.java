package io.github.gabrielpetry23.ecommerceapi.exceptions;

import lombok.Getter;

public class InvalidFieldException extends RuntimeException {

    @Getter
    private final String field;

    public InvalidFieldException(String field, String message) {
      super(message);
      this.field = field;
    }
}
