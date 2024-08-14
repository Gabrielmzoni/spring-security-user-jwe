package br.com.gmsoft.userjwe.service.exception;

public class InvalidBase64ContentException extends RuntimeException {
    public InvalidBase64ContentException(String message, Throwable cause) {
        super(message, cause);
    }
}