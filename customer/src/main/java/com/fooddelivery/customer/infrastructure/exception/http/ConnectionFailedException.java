package com.fooddelivery.customer.infrastructure.exception.http;

public class ConnectionFailedException extends RuntimeException {
    public ConnectionFailedException(String message) {
        super(message);
    }
}
