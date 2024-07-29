package com.fooddelivery.commerce.infrastructure.exception.rabbit;

public class ConnectionFailedException extends RuntimeException {
    public ConnectionFailedException(String message) {
        super(message);
    }
}