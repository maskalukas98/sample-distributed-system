package com.fooddelivery.commerce.infrastructure.service.internal;

import com.fooddelivery.commerce.application.exception.customer.CouldNotFetchCustomerException;
import com.fooddelivery.commerce.domain.port.output.CustomerServicePort;
import com.fooddelivery.commerce.infrastructure.exception.http.ConnectionFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;


@Service
public class HttpCustomerService implements CustomerServicePort {

    @Value("${customer.api.url}")
    private String apiUrl;

    private final String baseUrl = "/v1/customers";


    @Override
    @Retryable(
            maxAttempts = 2,
            include = ConnectionFailedException.class,
            backoff = @Backoff(delay = 500)
    )
    public boolean customerExistsById(int customerId)  {
        final RestTemplate restTemplate = new RestTemplate();
        final String fullApiUrl = apiUrl + baseUrl + "/" + customerId;

        // TODO: IMPLEMENT TIMER FOR MONITORING OF REQUESTS
        // time.start()

        try {
            final ResponseEntity<String> responseEntity = restTemplate.getForEntity(fullApiUrl, String.class);

            if (responseEntity.getStatusCode().value() == HttpStatus.OK.value()) {
                return true;
            } else {
                throw new HttpClientErrorException(responseEntity.getStatusCode());
            }
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (ResourceAccessException e) {
            throw new ConnectionFailedException();
        } catch (Exception e) {
            throw new CouldNotFetchCustomerException(e);
        }
    }
}
