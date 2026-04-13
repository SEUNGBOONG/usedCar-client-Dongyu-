package com.example.dongyucar.common;

/**
 * Simple not-found exception used by service/controller layers.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

