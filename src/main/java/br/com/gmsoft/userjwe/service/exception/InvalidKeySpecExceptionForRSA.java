package br.com.gmsoft.userjwe.service.exception;

public class InvalidKeySpecExceptionForRSA extends RuntimeException {
    public InvalidKeySpecExceptionForRSA(String message, Throwable cause) {
        super(message, cause);
    }
}
