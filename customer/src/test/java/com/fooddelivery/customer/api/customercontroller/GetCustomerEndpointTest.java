package com.fooddelivery.customer.api.customercontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fooddelivery.customer.common.http.ResponseData;
import com.fooddelivery.customer.helper.database.CustomerRepositoryTestHelper;
import com.fooddelivery.customer.infrastructure.contract.getcustomer.GetCustomerResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;


import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;



@SpringBootTest
@AutoConfigureMockMvc
public class GetCustomerEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepositoryTestHelper customerRepositoryTestHelper;

    @BeforeEach
    public void setup() {
        customerRepositoryTestHelper.clearAllTables();
    }

    @Test
    public void testGetCustomer_shouldReturnCustomer() throws Exception {
        // prepare
        customerRepositoryTestHelper.insertCustomer("john","doe","john@example.com");
        RequestBuilder request = get(TestCustomerConfig.baseEndpoint + "/1");

        // execute
        ResultActions result = mockMvc.perform(request);
        ResponseData<GetCustomerResponseDto> response = convertResultToResponseData(result.andReturn());

        // verify
        result.andExpect(MockMvcResultMatchers.status().isOk());
        Assertions.assertEquals(response.data().id(), 1);
        Assertions.assertEquals(response.data().name(), "john");
        Assertions.assertEquals(response.data().surname(), "doe");
        Assertions.assertEquals(response.data().email(), "john@example.com");
    }

    @Test
    public void testGetCustomer_shouldReturn404DueToNonExistentCustomer() throws Exception {
        // prepare
        RequestBuilder request = get(TestCustomerConfig.baseEndpoint + "/40");

        // execute
        ResultActions result = mockMvc.perform(request);

        // verify
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    private ResponseData<GetCustomerResponseDto> convertResultToResponseData(
            MvcResult mvcResult
    ) throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = mvcResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<ResponseData<GetCustomerResponseDto>> typeRef = new TypeReference<>() {};
        return objectMapper.readValue(responseContent, typeRef);
    }
}
