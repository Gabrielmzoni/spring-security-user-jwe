package br.com.gmsoft.userjwe.service.exception;

public class JweExpiredException extends RuntimeException {
    public JweExpiredException(String message) {
        super(message);
    }
}