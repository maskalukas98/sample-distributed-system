package com.fooddelivery.commerce.application.exception.customer;


public class CouldNotFetchCustomerException extends RuntimeException {
    private Exception e;
    public CouldNotFetchCustomerException(Exception e) {
        this.e = e;
    }
}
