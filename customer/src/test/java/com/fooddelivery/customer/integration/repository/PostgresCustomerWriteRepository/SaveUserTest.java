package com.fooddelivery.customer.integration.repository.PostgresCustomerWriteRepository;

import com.fooddelivery.customer.application.exception.DuplicateCustomerException;
import com.fooddelivery.customer.domain.aggregate.Customer;
import com.fooddelivery.customer.domain.entity.CustomerPreference;
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
public class SaveUserTest {

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
        Customer newCustomer = Customer.createNew("Lukas","Maska","lukas@email.com");

        // execute
        assertDoesNotThrow(() -> {
            postgresCustomerWriteRepository.saveCustomer(newCustomer);
        });

        // verify - customers table
        List<Customer> customers = customerRepositoryTestHelper.getAllCustomers();
        Customer createdCustomer = customers.get(0);
        assertEquals(customers.size(), 1);
        assertEquals(createdCustomer.getName(),"Lukas");
        assertEquals(createdCustomer.getSurname(),"Maska");
        assertEquals(createdCustomer.getEmail(),"lukas@email.com");

        // verify - customer_preferences table
        List<CustomerPreference> customerPreferences = customerRepositoryTestHelper.getAllCustomerPreferences();
        CustomerPreference createdCustomerPreference = customerPreferences.get(0);
        assertEquals(customerPreferences.size(), 1);
        assertTrue(createdCustomerPreference.isNotificationEmail());
        assertTrue(createdCustomerPreference.isNotificationSms());
        assertEquals(createdCustomerPreference.getLanguage(), "CZ");
    }

    @Test
    public void testSaveUser_shouldFailDueToDuplicateUser() {
        // prepare
        Customer newCustomer = Customer.createNew("Lukas","Maska","lukas@email.com");

        // execute
        postgresCustomerWriteRepository.saveCustomer(newCustomer);
        assertThrows(DuplicateCustomerException.class, () -> {
            postgresCustomerWriteRepository.saveCustomer(newCustomer);
        });

        // verify - customers table
        List<Customer> customers = customerRepositoryTestHelper.getAllCustomers();
        assertEquals(customers.size(), 1);

        // verify - customer_preferences table
        List<CustomerPreference> customerPreferences = customerRepositoryTestHelper.getAllCustomerPreferences();
        assertEquals(customerPreferences.size(), 1);
    }

    @Test
    public void testSaveUser_shouldCreateTwoUsers() {
        // prepare
        Customer firstCustomer = Customer.createNew("Lukas","Maska","lukas@email.com");
        Customer secondCustomer = Customer.createNew("Jan","Novak","jan@email.com");

        // execute
        assertDoesNotThrow(() -> {
            postgresCustomerWriteRepository.saveCustomer(firstCustomer);
            postgresCustomerWriteRepository.saveCustomer(secondCustomer);
        });

        // verify - customers table
        List<Customer> customers = customerRepositoryTestHelper.getAllCustomers();
        assertEquals(customers.size(), 2);

        // verify - customer_preferences table
        List<CustomerPreference> customerPreferences = customerRepositoryTestHelper.getAllCustomerPreferences();
        assertEquals(customerPreferences.size(), 2);
    }
}
