package com.fooddelivery.customer.domain.port.output;

import com.fooddelivery.customer.application.exception.DuplicateCustomerException;
import com.fooddelivery.customer.application.exception.FailedToSaveCustomerException;
import com.fooddelivery.customer.domain.aggregate.Customer;
import com.fooddelivery.customer.domain.constant.EmailSource;
import com.fooddelivery.customer.domain.valueobject.CustomerId;

public interface CustomerWriteRepositoryPort {
    int saveCustomer(Customer customer) throws DuplicateCustomerException, FailedToSaveCustomerException;
    public void insertFailedRegistrationEvent(CustomerId customerID, EmailSource emailSource);
}
