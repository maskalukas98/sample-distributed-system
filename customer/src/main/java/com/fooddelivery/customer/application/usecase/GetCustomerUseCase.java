package com.fooddelivery.customer.application.usecase;

import com.fooddelivery.customer.application.command.GetCustomerCommand;
import com.fooddelivery.customer.domain.aggregate.Customer;
import com.fooddelivery.customer.domain.port.input.GetCustomerPort;
import com.fooddelivery.customer.domain.port.output.CustomerReadRepositoryPort;
import com.fooddelivery.customer.domain.valueobject.CustomerId;
import org.springframework.stereotype.Service;

@Service
public final class GetCustomerUseCase implements GetCustomerPort {
    private final CustomerReadRepositoryPort customerReadRepository;

    public GetCustomerUseCase(
            CustomerReadRepositoryPort customerReadRepository
    ) {
        this.customerReadRepository = customerReadRepository;
    }
    @Override
    public Customer get(GetCustomerCommand command) {
        CustomerId customerId = new CustomerId(command.customerId());
        return customerReadRepository.getCustomerById(customerId);
    }
}
