package com.fooddelivery.customer.domain.port.input;

import com.fooddelivery.customer.application.command.CreateCustomerCommand;
import com.fooddelivery.customer.domain.valueobject.CustomerId;

public interface CreateCustomerPort {
    CustomerId create(CreateCustomerCommand command);
}
