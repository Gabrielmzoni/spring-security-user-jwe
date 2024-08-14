package br.com.gmsoft.userjwe.service.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("User not found");
    }

    public UserNotFoundException(String message) {
        super(String.format("User with email %s not found", message));
    }
}
