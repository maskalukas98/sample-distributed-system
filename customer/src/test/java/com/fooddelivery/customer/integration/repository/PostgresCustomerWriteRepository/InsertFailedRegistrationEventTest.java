package com.fooddelivery.customer.integration.repository.PostgresCustomerWriteRepository;

import com.fooddelivery.customer.domain.aggregate.Customer;
import com.fooddelivery.customer.domain.constant.EmailSource;
import com.fooddelivery.customer.domain.entity.EmailEventQueueFailure;
import com.fooddelivery.customer.domain.valueobject.CustomerId;
import com.fooddelivery.customer.infrastructure.repository.PostgresCustomerWriteRepository;
import com.fooddelivery.customer.helper.database.CustomerRepositoryTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InsertFailedRegistrationEventTest {

    @Autowired
    private PostgresCustomerWriteRepository postgresCustomerWriteRepository;

    @Autowired
    private CustomerRepositoryTestHelper customerRepositoryTestHelper;

    @BeforeEach
    public void setup() {
        customerRepositoryTestHelper.clearAllTables();
    }

    @Test
    public void testSaveUser_shouldSaveSuccessfully() {
        // prepare
        postgresCustomerWriteRepository.saveCustomer(Customer.createNew("John","Doe","john@email.com"));

        // execute
        assertDoesNotThrow(() -> {
            postgresCustomerWriteRepository.insertFailedRegistrationEvent(new CustomerId(1), EmailSource.Welcome);
        });

        // verify - email_event_queue_failures table
        List<EmailEventQueueFailure> failEvents = customerRepositoryTestHelper.getAllEmailFailEvents();
        EmailEventQueueFailure failEvent = failEvents.get(0);

        assertEquals(failEvents.size(), 1);
        assertEquals(failEvent.getCustomerId().getValue().intValue(),1);
        assertEquals(failEvent.getEmailSource().toString(), "Welcome");
    }
}
