package com.example.servigohex.domain.exceptions;

public class DuplicateCategoryNameException extends RuntimeException {
    public DuplicateCategoryNameException(String mensaje) {
        super(mensaje);
    }
}
