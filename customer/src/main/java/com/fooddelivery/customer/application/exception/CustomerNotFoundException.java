package com.fooddelivery.customer.application.exception;


public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String prop, String key) {
        super("Customer with the " + prop + " " + key + " was not found.");
    }
}
