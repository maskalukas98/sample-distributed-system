package com.fooddelivery.customer.application.mapper;

import com.fooddelivery.customer.application.command.CreateCustomerCommand;
import com.fooddelivery.customer.infrastructure.contract.createcustomer.CreateCustomerRequestDto;

public final class CreateCustomerMapper {
    public static CreateCustomerCommand createUserRequestToCommand(CreateCustomerRequestDto request) {
        return new CreateCustomerCommand(
                request.name(),
                request.surname(),
                request.email()
        );
    }
}
