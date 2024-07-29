package com.fooddelivery.customer.api.customercontroller;

import com.fooddelivery.customer.domain.aggregate.Customer;
import com.fooddelivery.customer.helper.database.CustomerRepositoryTestHelper;
import com.fooddelivery.customer.infrastructure.contract.createcustomer.CreateCustomerRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;



@SpringBootTest
@AutoConfigureMockMvc
public class CreateCustomerEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepositoryTestHelper customerRepositoryTestHelper;

    @BeforeEach
    public void setup() {
        customerRepositoryTestHelper.clearAllTables();
    }

    @Test
    public void testCreateCustomer_shouldCreateCustomer() throws Exception {
        // prepare
        final CreateCustomerRequestDto request = new CreateCustomerRequestDto("Lukas", "Maska", "lukas@email.com");
        final ObjectMapper objectMapper = new ObjectMapper();

        // execute
        ResultActions result = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // verify response
        result.andExpect(MockMvcResultMatchers.status().isCreated());


        // verify database
        List<Customer> customers = customerRepositoryTestHelper.getAllCustomers();
        Assertions.assertEquals(customers.size(), 1);
    }

    @Test
    public void testCreateCustomer_shouldFailDueToDuplicateCustomer() throws Exception {
        // prepare
        final CreateCustomerRequestDto firstRequest = new CreateCustomerRequestDto("Lukas", "Maska", "lukas@email.com");
        final CreateCustomerRequestDto secondRequest = new CreateCustomerRequestDto("Jan", "Novak", "lukas@email.com");
        final ObjectMapper objectMapper = new ObjectMapper();

        // execute
        ResultActions successResult = mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest)));

        ResultActions failResult = mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondRequest)));

        // verify response
        successResult.andExpect(MockMvcResultMatchers.status().isCreated());
        failResult.andExpect(MockMvcResultMatchers.status().isConflict());


        // verify database
        List<Customer> customers = customerRepositoryTestHelper.getAllCustomers();
        Assertions.assertEquals(customers.size(), 1);
    }

    @Test
    public void testCreateCustomer_shouldCreateMultipleCustomers() throws Exception {
        // prepare
        final CreateCustomerRequestDto firstRequest = new CreateCustomerRequestDto("Lukas", "Maska", "lukas@email.com");
        final CreateCustomerRequestDto secondRequest = new CreateCustomerRequestDto("Jan", "Novak", "jan@email.com");
        final ObjectMapper objectMapper = new ObjectMapper();

        // execute
        ResultActions successResult1 = mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest)));

        ResultActions successResult2 = mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondRequest)));

        // verify response
        successResult1.andExpect(MockMvcResultMatchers.status().isCreated());
        successResult2.andExpect(MockMvcResultMatchers.status().isCreated());


        // verify database
        List<Customer> customers = customerRepositoryTestHelper.getAllCustomers();
        Assertions.assertEquals(customers.size(), 2);
    }
}
