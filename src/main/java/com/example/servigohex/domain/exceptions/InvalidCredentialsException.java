package com.example.servigohex.domain.exceptions;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String mensaje) {
        super(mensaje);
    }
}
