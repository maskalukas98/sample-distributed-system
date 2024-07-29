package com.fooddelivery.customer.application.exception;

import com.fooddelivery.customer.domain.valueobject.CustomerId;
import lombok.Getter;
import lombok.Setter;

public class DuplicateCustomerException extends RuntimeException {
    @Getter
    @Setter
    private CustomerId conflictedUserId;

    public DuplicateCustomerException(String email) {
        super("User with email " + email + " is already created.");
    }
}
