package com.fooddelivery.customer.application.exception;

import com.fooddelivery.customer.domain.valueobject.CustomerId;

public class FailedToSaveCustomerException extends RuntimeException {
    public FailedToSaveCustomerException(String email, String m) {
        super("Could not save the new customer" + email + " due some error: " + m);
    }
}
