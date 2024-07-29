package com.fooddelivery.commerce.domain.port.output;

import com.fooddelivery.commerce.application.exception.customer.CouldNotFetchCustomerException;
import com.fooddelivery.commerce.infrastructure.exception.http.ConnectionFailedException;

public interface CustomerServicePort {
    boolean customerExistsById(int customerId) throws ConnectionFailedException, CouldNotFetchCustomerException;
}
