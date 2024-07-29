package com.fooddelivery.customer.domain.port.input;

import com.fooddelivery.customer.application.command.GetCustomerCommand;
import com.fooddelivery.customer.domain.aggregate.Customer;

public interface GetCustomerPort {
    Customer get(GetCustomerCommand command);
}
