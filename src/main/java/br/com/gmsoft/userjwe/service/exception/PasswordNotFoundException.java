package br.com.gmsoft.userjwe.service.exception;

public class PasswordNotFoundException extends RuntimeException {
    public PasswordNotFoundException() {
        super("Password not found");
    }

    public PasswordNotFoundException(String message) {
        super(String.format("Password with id %s not found", message));
    }
}
