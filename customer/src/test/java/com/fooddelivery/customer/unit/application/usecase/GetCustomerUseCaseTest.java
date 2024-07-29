package com.fooddelivery.customer.unit.application.usecase;

import com.fooddelivery.customer.application.command.GetCustomerCommand;
import com.fooddelivery.customer.application.exception.CustomerNotFoundException;
import com.fooddelivery.customer.application.usecase.GetCustomerUseCase;
import com.fooddelivery.customer.domain.aggregate.Customer;
import com.fooddelivery.customer.domain.port.output.CustomerReadRepositoryPort;
import com.fooddelivery.customer.domain.valueobject.CustomerId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetCustomerUseCaseTest {
    @Mock
    private CustomerReadRepositoryPort customerReadRepository;

    @InjectMocks
    private GetCustomerUseCase getCustomerUseCase;

    @ParameterizedTest
    @MethodSource("customersTestData")
    public void testGet_shouldReturnCustomerSuccessfully(
            int id,
            String name,
            String surname,
            String email
    ) {
        // prepare
        Customer loadedCustomer = (Customer) Customer.create(name,surname,email)
                                                     .setId(new CustomerId(id));
        when(customerReadRepository.getCustomerById(any()))
                .thenReturn(loadedCustomer);

        // execute
        Customer returnedCustomer = assertDoesNotThrow(() -> {
            return getCustomerUseCase.get(new GetCustomerCommand(id));
        });

            // verify
        Assertions.assertEquals(returnedCustomer.getId().getValue(), id);
        Assertions.assertEquals(returnedCustomer.getName(), name);
        Assertions.assertEquals(returnedCustomer.getSurname(), surname);
        Assertions.assertEquals(returnedCustomer.getEmail(), email);
    }

    @Test
    public void testGet_shouldThrowExceptionDueToNotFoundCustomer( ) {
        // prepare
        when(customerReadRepository.getCustomerById(any()))
                .thenThrow(CustomerNotFoundException.class);

        // execute + verify
        assertThrows(CustomerNotFoundException.class, () -> {
            getCustomerUseCase.get(new GetCustomerCommand(1));
        });
    }

    private static Stream<Arguments> customersTestData() {
        return Stream.of(
                Arguments.of(1, "Lukas", "Maska", "lukas@email.com"),
                Arguments.of(2, "Jan", "Novak", "jan@email.com")
        );
    }
}
