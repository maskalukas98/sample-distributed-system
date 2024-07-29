package com.fooddelivery.customer.domain.exception;

public class HatefulNameViolationException extends RuntimeException {
    public HatefulNameViolationException(String name) {
        super("The name %s is not allowed because it is hateful.".formatted(name));
    }
}
