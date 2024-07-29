package com.fooddelivery.commerce.infrastructure.exception.rabbit;

public class EventSendFailedException extends RuntimeException {
    public EventSendFailedException(String message) {
        super(message);
    }
}
