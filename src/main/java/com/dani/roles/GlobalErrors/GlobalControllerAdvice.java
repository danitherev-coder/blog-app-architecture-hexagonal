package com.dani.roles.GlobalErrors;

import com.dani.roles.domain.exception.LoginException;
import com.dani.roles.domain.exception.UnauthorizedAccessException;
import com.dani.roles.domain.exception.UserNotFoundException;
import com.dani.roles.domain.model.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.dani.roles.utils.ErrorCategorizado.*;

@RestControllerAdvice
public class GlobalControllerAdvice {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException ex) {
        return ErrorResponse.builder()
                .code(USER_NOT_FOUND.getCode())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LoginException.class)
    public ErrorResponse handleLoginException(LoginException ex) {
        return ErrorResponse.builder()
                .code("LOGIN_ERROR_CREDENTIALS")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ErrorResponse handleUnauthorizedAccessException() {
        return ErrorResponse.builder()
                .code(UNAUTHORIZED_ACCESS.getCode())
                .message(UNAUTHORIZED_ACCESS.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex) {
        return ErrorResponse.builder()
                .code("ENTITY_NOT_FOUND")
                .message(ex.getMessage())
                .details(Collections.singletonList(ex.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicateKeyException.class)
    public ErrorResponse handleDuplicateKeyException(DuplicateKeyException ex) {
        return ErrorResponse.builder()
                .code(USER_ALREADY_EXISTS.getCode())
                .message(USER_NOT_CREATED.getMessage())
                .details(Collections.singletonList(ex.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        return ErrorResponse.builder()
                .code(USER_NOT_CREATED.getCode())
                .message(USER_NOT_CREATED.getMessage())
                .details(result.getFieldErrors().stream().map(fieldError -> fieldError.getDefaultMessage()).collect(Collectors.toList()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    /*
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception ex) {
        return ErrorResponse.builder()
                .code("GENERIC_ERROR")
                .message(ex.getMessage())
                .details(Collections.singletonList(ex.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
    }
*/
}
