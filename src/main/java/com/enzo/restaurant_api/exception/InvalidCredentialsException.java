package com.enzo.restaurant_api.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Credenciais inválidas.");
    }
}
