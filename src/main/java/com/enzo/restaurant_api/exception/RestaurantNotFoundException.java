package com.enzo.restaurant_api.exception;

public class RestaurantNotFoundException extends RuntimeException {

    public RestaurantNotFoundException(Long id) {
        super("Restaurante com ID " + id + " não foi encontrado.");
    }
}
