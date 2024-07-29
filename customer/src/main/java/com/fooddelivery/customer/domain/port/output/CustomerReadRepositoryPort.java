package com.fooddelivery.customer.domain.port.output;

import com.fooddelivery.customer.application.exception.CustomerNotFoundException;
import com.fooddelivery.customer.domain.aggregate.Customer;
import com.fooddelivery.customer.domain.valueobject.CustomerId;

public interface CustomerReadRepositoryPort {
    Customer getCustomerById(CustomerId customerId) throws CustomerNotFoundException;
    Customer getCustomerByEmail(String email) throws CustomerNotFoundException;
}
