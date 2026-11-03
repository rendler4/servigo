package com.example.servigohex.domain.exceptions;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String mensaje) {
        super(mensaje);
    }
}
