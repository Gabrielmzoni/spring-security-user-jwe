package br.com.gmsoft.userjwe.service.exception;

public class InvalidJweException extends RuntimeException {
    public InvalidJweException(String message) {
        super(message);
    }
}