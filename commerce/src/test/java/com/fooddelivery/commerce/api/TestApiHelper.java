package com.fooddelivery.commerce.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.commerce.domain.partner.constant.Country;
import com.fooddelivery.commerce.infrastructure.contract.order.createorder.CreateOrderRequestDto;
import com.fooddelivery.commerce.infrastructure.contract.order.createorder.CreateOrderResponseDto;
import com.fooddelivery.commerce.infrastructure.contract.partner.createpartner.CreatePartnerRequestDto;
import com.fooddelivery.commerce.infrastructure.contract.partner.createpartner.CreatePartnerResponseDto;
import com.fooddelivery.commerce.infrastructure.contract.product.createproduct.CreateProductRequestDto;
import com.fooddelivery.commerce.infrastructure.contract.product.createproduct.CreateProductResponseDto;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TestApiHelper {
    public static int createPartner(MockMvc mockMvc, Country country, String name) throws Exception {
        final ResultActions partnerResponse = mockMvc.perform(post(TestApiConfig.partnersApi)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        new ObjectMapper().writeValueAsString(
                                new CreatePartnerRequestDto(
                                        name,
                                        country
                                )
                        )
                )
        );

        MockHttpServletResponse r = partnerResponse.andReturn().getResponse();
        final String responseBody = r.getContentAsString();
        CreatePartnerResponseDto partner = new ObjectMapper().readValue(responseBody, CreatePartnerResponseDto.class);
        return partner.partnerId();
    }

    public static int createProduct(MockMvc mockMvc, Country country, int partnerId) throws Exception {
        final ResultActions partnerResponse = mockMvc.perform(post(TestApiConfig.productsApi)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        new ObjectMapper().writeValueAsString(
                                new CreateProductRequestDto(
                                        partnerId,
                                        "Hamburger",
                                        new BigDecimal(10),
                                        "EUR"
                                )
                        )
                )
        );

        final String responseBody = partnerResponse.andReturn().getResponse().getContentAsString();
        CreateProductResponseDto partner = new ObjectMapper().readValue(responseBody, CreateProductResponseDto.class);
        return partner.productId();
    }

    public static long createOrder(MockMvc mockMvc, Country country, int productId) throws Exception {
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

        return orderBody.orderId();
    }
}
