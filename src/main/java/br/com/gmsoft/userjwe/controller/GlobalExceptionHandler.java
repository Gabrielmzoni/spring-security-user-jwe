package br.com.gmsoft.userjwe.controller;

import br.com.gmsoft.userjwe.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_DESCRIPTION = "error";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleAuthorizationExceptions(
           BadCredentialsException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ERROR_DESCRIPTION, ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleFindUserExceptions(
            UserNotFoundException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ERROR_DESCRIPTION, ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleFindUserExceptions(
            UserAlreadyExistsException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ERROR_DESCRIPTION, ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PasswordNotFoundException.class)
    public ResponseEntity<Object> handleFindUserExceptions(
            PasswordNotFoundException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ERROR_DESCRIPTION, ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleGenericIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ERROR_DESCRIPTION, ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleGenericRuntimeException(
            RuntimeException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ERROR_DESCRIPTION, ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({EncryptionException.class,
            InvalidBase64ContentException.class,
            InvalidKeySpecExceptionForRSA.class,
            RSAAlgorithmException.class})
    public ResponseEntity<Object> handleSpecificRuntimeExceptions(
            RuntimeException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ERROR_DESCRIPTION, ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }



}