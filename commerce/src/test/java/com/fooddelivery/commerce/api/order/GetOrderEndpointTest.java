package com.fooddelivery.commerce.api.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fooddelivery.commerce.api.TestApiConfig;
import com.fooddelivery.commerce.api.TestApiHelper;
import com.fooddelivery.commerce.helper.TestPartnerRepositoryHelper;
import com.fooddelivery.commerce.helper.TestOrderRepositoryHelper;
import com.fooddelivery.commerce.domain.partner.constant.Country;
import com.fooddelivery.commerce.infrastructure.contract.order.getorder.GetOrderResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@SpringBootTest
@AutoConfigureMockMvc
public class GetOrderEndpointTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestPartnerRepositoryHelper partnerRepositoryTestHelper;

    @Autowired
    private TestOrderRepositoryHelper orderReadRepository;

    @BeforeEach
    public void setup() {
        partnerRepositoryTestHelper.clearAllTables("de");
        partnerRepositoryTestHelper.clearAllTables("fr");
        orderReadRepository.clearAllTables();
    }

    @ParameterizedTest
    @MethodSource(value = "shardCountriesData")
    public void testGetOrder_shouldCreateOrderInShard(Country country) throws Exception {
        // prepare
        final int partnerId = TestApiHelper.createPartner(mockMvc, country, "Fast pizza delivery");
        final int productId = TestApiHelper.createProduct(mockMvc, country, partnerId);
        final long orderId = TestApiHelper.createOrder(mockMvc, country, productId);

        // execute
        final ResultActions orderResponse = mockMvc.perform(get(TestApiConfig.ordersApi + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON));

        // verify response
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // verify response
        final GetOrderResponseDto orderBody = objectMapper.readValue(
                orderResponse.andReturn().getResponse().getContentAsString(),
                GetOrderResponseDto.class
        );

        orderResponse.andExpect(MockMvcResultMatchers.status().isOk());
        Assertions.assertEquals(orderBody.productId(), productId);
        Assertions.assertEquals(orderBody.customerId(), 1);
        Assertions.assertEquals(orderBody.id(), orderId);
        Assertions.assertEquals(orderBody.statuses().size(), 1);
        Assertions.assertEquals(orderBody.address(), "Prague 11");
    }

    @Test
    public void testGetOrder_shouldReturn404DueNotExistsOrder() throws Exception {
        // execute
        final ResultActions orderResponse = mockMvc.perform(get(TestApiConfig.ordersApi + "/1" )
                .contentType(MediaType.APPLICATION_JSON));

        // verify response
        orderResponse.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    private static Stream<Arguments> shardCountriesData() {
        return Stream.of(
                Arguments.of("de"),
                Arguments.of("fr")
        );
    }
}