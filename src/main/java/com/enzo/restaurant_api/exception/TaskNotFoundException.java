package com.enzo.restaurant_api.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("Task com ID " + id + " não foi encontrada.");
    }
}
