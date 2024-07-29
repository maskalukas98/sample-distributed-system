package com.fooddelivery.customer.smoke;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@SpringBootTest(webEnvironment = RANDOM_PORT)
public class HealthEndpointSmokeTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${health.endpoint.url}")
    private String healthUrl;

    @Test
    public void testHealthEndpoint() {
        ResponseEntity<String> response = restTemplate.getForEntity(healthUrl, String.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
}