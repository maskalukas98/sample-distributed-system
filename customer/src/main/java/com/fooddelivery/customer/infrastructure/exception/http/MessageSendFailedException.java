package com.fooddelivery.customer.infrastructure.exception.http;

public class MessageSendFailedException extends RuntimeException {
    public MessageSendFailedException(String message) {
        super(message);
    }
}
