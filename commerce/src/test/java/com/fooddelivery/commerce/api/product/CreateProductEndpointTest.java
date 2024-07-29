package com.fooddelivery.commerce.api.product;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.commerce.api.TestApiConfig;
import com.fooddelivery.commerce.api.TestApiHelper;
import com.fooddelivery.commerce.helper.TestPartnerRepositoryHelper;
import com.fooddelivery.commerce.helper.TestProductRepositoryHelper;
import com.fooddelivery.commerce.domain.partner.constant.Country;
import com.fooddelivery.commerce.domain.product.ProductAggregate;
import com.fooddelivery.commerce.domain.product.entity.ProductEntity;
import com.fooddelivery.commerce.infrastructure.contract.product.createproduct.CreateProductRequestDto;
import com.fooddelivery.commerce.infrastructure.contract.product.createproduct.CreateProductResponseDto;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;


@SpringBootTest
@AutoConfigureMockMvc
public class CreateProductEndpointTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestPartnerRepositoryHelper partnerRepositoryTestHelper;

    @Autowired
    private TestProductRepositoryHelper productRepositoryTestHelper;

    @BeforeEach
    public void setup() {
        partnerRepositoryTestHelper.clearAllTables("de");
        partnerRepositoryTestHelper.clearAllTables("fr");
    }

    @ParameterizedTest
    @MethodSource(value = "shardCountriesData")
    public void testCreatProduct_shouldCreateProductInShard(Country country) throws Exception {
        // prepare - create partner
        int partnerId = TestApiHelper.createPartner(mockMvc, country, "Fast pizza delivery");

        // prepare
        final CreateProductRequestDto request = new CreateProductRequestDto(
                partnerId,
                "Hamburger",
                new BigDecimal(10),
                "EUR"
        );

        // execute
        final ResultActions productResponse = mockMvc.perform(post(TestApiConfig.productsApi)
                .contentType(MediaType.APPLICATION_JSON)
                .content( new ObjectMapper().writeValueAsString(request)));

        CreateProductResponseDto productBody = new ObjectMapper().readValue(
                                                        productResponse.andReturn().getResponse().getContentAsString(),
                                                        CreateProductResponseDto.class
                                                );

        // verify response
        productResponse.andExpect(MockMvcResultMatchers.status().isCreated());
        Assertions.assertTrue(productBody.productId() > 0);

        // verify database
        List<ProductAggregate> products = productRepositoryTestHelper.getAllProducts(String.valueOf(country));
        ProductEntity product = products.get(0).getProduct();
        Assertions.assertEquals(products.size(), 1);
        Assertions.assertEquals(product.getProductName(), "Hamburger");
        Assertions.assertEquals(product.getPartnerId().getValue(), partnerId);
        Assertions.assertTrue(product.isActive());
        Assertions.assertEquals(product.getPrice(), new BigDecimal("10.00"));
    }

    @ParameterizedTest
    @MethodSource(value = "shardCountriesData")
    public void testCreatProduct_shouldCreateMultipleProducts(Country country) throws Exception {
        // prepare - create partner
        int partnerId = TestApiHelper.createPartner(mockMvc, country,"Fast delivery");

        // prepare
        final CreateProductRequestDto request1 = new CreateProductRequestDto(
                partnerId,
                "Hamburger",
                new BigDecimal(10),
                "EUR"
        );

        final CreateProductRequestDto request2 = new CreateProductRequestDto(
                partnerId,
                "Pizza",
                new BigDecimal(20),
                "EUR"
        );

        // execute
        final ResultActions productResponse1 = mockMvc.perform(post(TestApiConfig.productsApi)
                .contentType(MediaType.APPLICATION_JSON)
                .content( new ObjectMapper().writeValueAsString(request1)));

        final ResultActions productResponse2 = mockMvc.perform(post(TestApiConfig.productsApi)
                .contentType(MediaType.APPLICATION_JSON)
                .content( new ObjectMapper().writeValueAsString(request1)));


        // verify response
        productResponse1.andExpect(MockMvcResultMatchers.status().isCreated());
        productResponse2.andExpect(MockMvcResultMatchers.status().isCreated());

        // verify database
        List<ProductAggregate> products = productRepositoryTestHelper.getAllProducts(String.valueOf(country));
        Assertions.assertEquals(products.size(), 2);
    }

    @Test
    public void testCreatProduct_shouldReturn422DueToNotExistsPartner() throws Exception {
        // prepare
        final CreateProductRequestDto request = new CreateProductRequestDto(
                1,
                "Hamburger",
                new BigDecimal(10),
                "EUR"
        );

        // execute
        ResultActions result = mockMvc.perform(post(TestApiConfig.productsApi)
                .contentType(MediaType.APPLICATION_JSON)
                .content( new ObjectMapper().writeValueAsString(request)));

        // verify response
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    private static Stream<Arguments> shardCountriesData() {
        return Stream.of(
                Arguments.of("de"),
                Arguments.of("fr")
        );
    }
}
