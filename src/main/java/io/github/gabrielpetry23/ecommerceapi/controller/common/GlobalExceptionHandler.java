package io.github.gabrielpetry23.ecommerceapi.controller.common;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.CustomFieldError;
import io.github.gabrielpetry23.ecommerceapi.controller.dto.ResponseError;
import io.github.gabrielpetry23.ecommerceapi.exceptions.InvalidFieldException;
import io.github.gabrielpetry23.ecommerceapi.exceptions.OperationNotAllowedException;
import io.github.gabrielpetry23.ecommerceapi.exceptions.DuplicateRecordException;
import io.github.gabrielpetry23.ecommerceapi.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseError handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getFieldErrors();
        List<CustomFieldError> errosList = fieldErrors
                .stream()
                .map(fe -> new CustomFieldError(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());

        return new ResponseError(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Erro de validação", errosList);
    }

    @ExceptionHandler(DuplicateRecordException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseError handleDuplicateRecordException(DuplicateRecordException ex) {
        return ResponseError.conflict(ex.getMessage());
    }

    @ExceptionHandler(OperationNotAllowedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handleOperationNotAllowed(OperationNotAllowedException ex) {
        return ResponseError.defaultResponse(ex.getMessage());
    }

    @ExceptionHandler(InvalidFieldException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseError handleInvalidFieldException(InvalidFieldException ex) {
        return new ResponseError(
                HttpStatus.UNPROCESSABLE_ENTITY.value(), "Validation error", List.of(new CustomFieldError(ex.getField(), ex.getMessage())));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseError handleAcessDeniedException(AccessDeniedException ex) {
        return new ResponseError(HttpStatus.FORBIDDEN.value(), "Access denied.", List.of());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseError handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseError(HttpStatus.NOT_FOUND.value(), ex.getMessage(), List.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseError.defaultResponse(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseError handleUnhandledError(RuntimeException ex) {

        log.error("Unexpected error: {}", ex.getMessage());

        return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unexpected error! Please contact support. ", List.of());
    }
}
