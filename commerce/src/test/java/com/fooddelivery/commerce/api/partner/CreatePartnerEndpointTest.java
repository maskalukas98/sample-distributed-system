package com.fooddelivery.commerce.api.partner;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.commerce.api.TestApiConfig;
import com.fooddelivery.commerce.helper.TestPartnerRepositoryHelper;
import com.fooddelivery.commerce.domain.partner.PartnerAggregate;
import com.fooddelivery.commerce.domain.partner.constant.Country;
import com.fooddelivery.commerce.infrastructure.contract.partner.createpartner.CreatePartnerRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
public class CreatePartnerEndpointTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestPartnerRepositoryHelper partnerRepositoryTestHelper;
    @BeforeEach
    public void setup() {
        partnerRepositoryTestHelper.clearAllTables("de");
        partnerRepositoryTestHelper.clearAllTables("fr");
    }

    @Test
    public void testCreatPartner_shouldCreatePartnerInFRShard() throws Exception {
        // prepare
        final CreatePartnerRequestDto request = new CreatePartnerRequestDto("Fast pizza delivery", Country.fr);
        final ObjectMapper objectMapper = new ObjectMapper();

        // execute
        ResultActions result = mockMvc.perform(post(TestApiConfig.partnersApi)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // verify response
        result.andExpect(MockMvcResultMatchers.status().isCreated());

        // verify database
        List<PartnerAggregate> partners = partnerRepositoryTestHelper.getAllPartners("fr");
        PartnerAggregate newPartner = partners.get(0);
        Assertions.assertEquals(partners.size(), 1);
        Assertions.assertEquals(newPartner.getCompanyName(), "Fast pizza delivery");
    }

    @Test
    public void testCreatPartner_shouldCreatePartnerInDEShard() throws Exception {
        // prepare
        final CreatePartnerRequestDto request = new CreatePartnerRequestDto("Fish delivery", Country.de);
        final ObjectMapper objectMapper = new ObjectMapper();

        // execute
        ResultActions result = mockMvc.perform(post(TestApiConfig.partnersApi)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // verify response
        result.andExpect(MockMvcResultMatchers.status().isCreated());

        // verify database
        List<PartnerAggregate> partners = partnerRepositoryTestHelper.getAllPartners("de");
        PartnerAggregate newPartner = partners.get(0);
        Assertions.assertEquals(partners.size(), 1);
        Assertions.assertEquals(newPartner.getCompanyName(), "Fish delivery");
    }

    @Test
    public void testCreatPartner_shouldCreateTwoPartnersInFRShard() throws Exception {
        // prepare
        final CreatePartnerRequestDto request1 = new CreatePartnerRequestDto("Ice cream delivery", Country.fr);
        final CreatePartnerRequestDto request2 = new CreatePartnerRequestDto("Fish delivery", Country.fr);

        // execute
        final ResultActions response1 = mockMvc.perform(post(TestApiConfig.partnersApi)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request1)));

        final ResultActions response2 = mockMvc.perform(post(TestApiConfig.partnersApi)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request2)));

        // verify response
        response1.andExpect(MockMvcResultMatchers.status().isCreated());
        response2.andExpect(MockMvcResultMatchers.status().isCreated());

        // verify database
        List<PartnerAggregate> partners = partnerRepositoryTestHelper.getAllPartners("fr");
        Assertions.assertEquals(partners.size(), 2);
        Assertions.assertEquals(partners.get(0).getCompanyName(), "Ice cream delivery");
        Assertions.assertEquals(partners.get(1).getCompanyName(), "Fish delivery");
    }

    @Test
    public void testCreatPartner_shouldReturn409DueConflictPartner() throws Exception {
        // prepare
        final CreatePartnerRequestDto request1 = new CreatePartnerRequestDto("Ice cream delivery", Country.fr);
        final CreatePartnerRequestDto request2 = new CreatePartnerRequestDto("Ice cream delivery", Country.fr);

        // execute
        final ResultActions response1 = mockMvc.perform(post(TestApiConfig.partnersApi)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request1)));

        final ResultActions response2 = mockMvc.perform(post(TestApiConfig.partnersApi)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request2)));

        // verify response
        response1.andExpect(MockMvcResultMatchers.status().isCreated());
        response2.andExpect(MockMvcResultMatchers.status().isConflict());

        // verify database
        List<PartnerAggregate> partners = partnerRepositoryTestHelper.getAllPartners("fr");
        Assertions.assertEquals(partners.size(), 1);
        Assertions.assertEquals(partners.get(0).getCompanyName(), "Ice cream delivery");
    }
}
