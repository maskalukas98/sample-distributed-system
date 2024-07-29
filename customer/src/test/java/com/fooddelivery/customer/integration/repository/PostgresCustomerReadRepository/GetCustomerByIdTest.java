package com.fooddelivery.customer.integration.repository.PostgresCustomerReadRepository;

import com.fooddelivery.customer.application.exception.CustomerNotFoundException;
import com.fooddelivery.customer.domain.aggregate.Customer;
import com.fooddelivery.customer.domain.valueobject.CustomerId;
import com.fooddelivery.customer.helper.database.type.RowId;
import com.fooddelivery.customer.infrastructure.repository.PostgresCustomerReadRepository;
import com.fooddelivery.customer.helper.database.CustomerRepositoryTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
@RunWith(SpringRunner.class)
@SpringBootTest
public class GetCustomerByIdTest {

    @Autowired
    private PostgresCustomerReadRepository postgresCustomerReadRepository;

    @Autowired
    private CustomerRepositoryTestHelper customerRepositoryTestHelper;

    @BeforeEach
    public void setup() {
        customerRepositoryTestHelper.clearAllTables();
    }

    @Test
    public void testGetCustomerById_shouldGetCustomerSuccessfully() {
        // prepare
        customerRepositoryTestHelper.insertCustomer("joe", "doe", "joe@example.com");
        CustomerId customerId = new CustomerId(1);

        // execute
        Customer selectedCustomer = assertDoesNotThrow(() -> {
            return postgresCustomerReadRepository.getCustomerById(customerId);
        });

        // verify
        assertEquals(selectedCustomer.getName(), "joe");
        assertEquals(selectedCustomer.getSurname(), "doe");
        assertEquals(selectedCustomer.getEmail(), "joe@example.com");
    }

    @ParameterizedTest
    @ValueSource(ints = {10,50,100})
    public void testGetCustomerById_shouldThrowsNotFoundDueCustomerNotFound(int id) {
        // prepare
        CustomerId customerId = new CustomerId(id);

        // execute + verify
        assertThrows(CustomerNotFoundException.class, () -> {
            postgresCustomerReadRepository.getCustomerById(customerId);
        });
    }
}
