package com.fooddelivery.commerce.api.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.commerce.api.TestApiConfig;
import com.fooddelivery.commerce.api.TestApiHelper;
import com.fooddelivery.commerce.helper.TestPartnerRepositoryHelper;
import com.fooddelivery.commerce.helper.TestOrderRepositoryHelper;
import com.fooddelivery.commerce.domain.order.OrderAggregate;
import com.fooddelivery.commerce.domain.order.constant.OrderStatus;
import com.fooddelivery.commerce.domain.partner.constant.Country;
import com.fooddelivery.commerce.infrastructure.contract.order.createorder.CreateOrderRequestDto;
import com.fooddelivery.commerce.infrastructure.contract.order.createorder.CreateOrderResponseDto;
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

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest
@AutoConfigureMockMvc
public class CreateOrderEndpointTest {
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
    public void testCreatOrder_shouldCreateOrderInShard(Country country) throws Exception {
        // prepare
        final int partnerId = TestApiHelper.createPartner(mockMvc, country, "Fast pizza delivery");
        final int productId = TestApiHelper.createProduct(mockMvc, country, partnerId);

        CreateOrderRequestDto request = new CreateOrderRequestDto(
                productId,
                1,
                "Prague 11"
        );

        // execute
        final ResultActions orderResponse = mockMvc.perform(post(TestApiConfig.ordersApi)
                .contentType(MediaType.APPLICATION_JSON)
                .content( new ObjectMapper().writeValueAsString(request)));

        // verify response
        final CreateOrderResponseDto orderBody = new ObjectMapper().readValue(
                orderResponse.andReturn().getResponse().getContentAsString(),
                CreateOrderResponseDto.class
        );
        orderResponse.andExpect(MockMvcResultMatchers.status().isCreated());
        Assertions.assertTrue(orderBody.orderId() > 0);

        // verify database
        final List<OrderAggregate> orders = orderReadRepository.getAllOrders(String.valueOf(country));
        final OrderAggregate order = orders.get(0);
        Assertions.assertEquals(orders.size(), 1);
        Assertions.assertEquals(order.getStatuses().size(), 1);
        Assertions.assertEquals(order.getStatuses().get(0).getStatus(), OrderStatus.Status.created);
    }

    @Test
    public void testCreatOrder_shouldReturn422DueNotExistsPartner() throws Exception {
        // prepare
        CreateOrderRequestDto request = new CreateOrderRequestDto(
                1,
                1,
                "Prague 11"
        );

        // execute
        final ResultActions orderResponse = mockMvc.perform(post(TestApiConfig.ordersApi)
                .contentType(MediaType.APPLICATION_JSON)
                .content( new ObjectMapper().writeValueAsString(request)));

        // verify response
        orderResponse.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());

        // verify database
        final List<OrderAggregate> orders = orderReadRepository.getAllOrders("fr");
        Assertions.assertEquals(orders.size(), 0);
    }

    @Test
    public void testCreatProduct_shouldReturn422DueNotExistsCustomer() throws Exception {
        // prepare
        CreateOrderRequestDto request = new CreateOrderRequestDto(
                1,
                2,
                "Prague 11"
        );

        // execute
        final ResultActions orderResponse = mockMvc.perform(post(TestApiConfig.ordersApi)
                .contentType(MediaType.APPLICATION_JSON)
                .content( new ObjectMapper().writeValueAsString(request)));

        // verify response
        orderResponse.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());

        // verify database
        final List<OrderAggregate> orders = orderReadRepository.getAllOrders("fr");
        Assertions.assertEquals(orders.size(), 0);
    }

    private static Stream<Arguments> shardCountriesData() {
        return Stream.of(
                Arguments.of("de"),
                Arguments.of("fr")
        );
    }
}