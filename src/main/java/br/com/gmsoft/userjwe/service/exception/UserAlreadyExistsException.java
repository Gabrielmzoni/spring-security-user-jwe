package br.com.gmsoft.userjwe.service.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("User already exists");
    }

    public UserAlreadyExistsException(String data, String dataContent) {
        super(String.format("User with %s %s already exists", data, dataContent));
    }
}
