package com.fooddelivery.commerce.application.exception.customer;

public class CustomerNotExistsException extends RuntimeException {
    public CustomerNotExistsException(int customerId) {
        super("Customer with id " + customerId + " not exists.");
    }
}
