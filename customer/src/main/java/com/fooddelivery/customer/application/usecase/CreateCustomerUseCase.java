package com.fooddelivery.customer.application.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fooddelivery.customer.application.command.CreateCustomerCommand;
import com.fooddelivery.customer.application.event.RegistrationSuccessEvent;
import com.fooddelivery.customer.application.exception.DuplicateCustomerException;
import com.fooddelivery.customer.application.exception.FailedToSaveCustomerException;
import com.fooddelivery.customer.common.logger.BusinessLogger;
import com.fooddelivery.customer.domain.aggregate.Customer;
import com.fooddelivery.customer.domain.constant.EmailSource;
import com.fooddelivery.customer.domain.port.input.CreateCustomerPort;
import com.fooddelivery.customer.domain.port.output.CustomerReadRepositoryPort;
import com.fooddelivery.customer.domain.port.output.CustomerWriteRepositoryPort;
import com.fooddelivery.customer.domain.port.output.NotificationQueuePort;
import com.fooddelivery.customer.domain.valueobject.CustomerId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public final class CreateCustomerUseCase implements CreateCustomerPort {
    private final Logger logger = LoggerFactory.getLogger(CreateCustomerUseCase.class);
    private final BusinessLogger businessLogger;
    private final CustomerWriteRepositoryPort customerWriteRepository;
    private final CustomerReadRepositoryPort customerReadRepository;
    private final NotificationQueuePort notificationQueueService;

    public CreateCustomerUseCase(
            CustomerWriteRepositoryPort customerWriteRepository,
            CustomerReadRepositoryPort customerReadRepository,
            NotificationQueuePort notificationQueuePort,
            BusinessLogger businessLogger
    ) {
        this.customerWriteRepository = customerWriteRepository;
        this.customerReadRepository = customerReadRepository;
        this.notificationQueueService = notificationQueuePort;
        this.businessLogger = businessLogger;
    }

    @Override
    public CustomerId create(CreateCustomerCommand command) {
        final Customer newCustomer = Customer.createNew(command.name(), command.surname(), command.email());
        CustomerId customerId;

        try {
            final int generatedCustomerId = customerWriteRepository.saveCustomer(newCustomer);
            customerId = new CustomerId(generatedCustomerId);
        } catch (DuplicateCustomerException e) {
            handleDuplicateCustomerException(e, newCustomer.getEmail());
            throw e;
        } catch (FailedToSaveCustomerException e) {
            throw e;
        }

        try {
            notificationQueueService.pushRegistrationEmailToQueue(
                    new RegistrationSuccessEvent(
                            customerId.getValue(),
                            newCustomer.getFullName(),
                            newCustomer.getEmail()
                    )
            );
        } catch (Exception e) {
            customerWriteRepository.insertFailedRegistrationEvent(customerId, EmailSource.Welcome);
        }

        final ObjectNode userNode = new ObjectMapper().createObjectNode()
                .put("customerId", customerId.getValue())
                // TODO: Create created_at in the database!
                .put("created_at", LocalDateTime.now().toString())
                // TODO: Create country in the database!
                .put("country", "US");

        businessLogger.logInfo("created_user", String.valueOf(userNode));

        return customerId;
    }

    private void handleDuplicateCustomerException(DuplicateCustomerException e, String email) {
        try {
            Customer alreadyCreatedUser = customerReadRepository.getCustomerByEmail(email);
            e.setConflictedUserId(alreadyCreatedUser.getId());
        } catch (Exception ex) {
            logger.error("Unable to load customer by email " + email + " for HATEOAS.", ex);
        }
    }
}
