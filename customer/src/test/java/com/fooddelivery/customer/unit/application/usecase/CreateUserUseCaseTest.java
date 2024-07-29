package com.fooddelivery.customer.unit.application.usecase;

import com.fooddelivery.customer.application.command.CreateCustomerCommand;
import com.fooddelivery.customer.application.event.RegistrationSuccessEvent;
import com.fooddelivery.customer.application.exception.DuplicateCustomerException;
import com.fooddelivery.customer.application.usecase.CreateCustomerUseCase;
import com.fooddelivery.customer.common.logger.BusinessLogger;
import com.fooddelivery.customer.domain.aggregate.Customer;
import com.fooddelivery.customer.domain.constant.EmailSource;
import com.fooddelivery.customer.domain.port.output.CustomerWriteRepositoryPort;
import com.fooddelivery.customer.domain.port.output.NotificationQueuePort;
import com.fooddelivery.customer.domain.valueobject.CustomerId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateUserUseCaseTest {
    @Mock
    private CustomerWriteRepositoryPort customerWriteRepository;

    @Mock
    private NotificationQueuePort notificationQueueService;

    @Mock
    private BusinessLogger businessLogger;

    @InjectMocks
    private CreateCustomerUseCase createUserUseCase;

    private ArgumentCaptor<RegistrationSuccessEvent> eventCaptor;

    private CreateCustomerCommand command;

    @BeforeEach
    public void setup() {
        eventCaptor = ArgumentCaptor.forClass(RegistrationSuccessEvent.class);
        command = new CreateCustomerCommand("Lukas","Maska","lukas@email.com");
    }

    @Test
    public void testCreate_success() {
        // prepare
        final Integer generatedUserId = 51;
        when(customerWriteRepository.saveCustomer(any(Customer.class)))
                .thenReturn(generatedUserId);

        // execute
        final CustomerId returnedNewUserId = createUserUseCase.create(command);

        // verify
        Assertions.assertEquals(returnedNewUserId.getValue(), generatedUserId);
        Mockito.verify(notificationQueueService, VerificationModeFactory.times(1))
                .pushRegistrationEmailToQueue(any(RegistrationSuccessEvent.class));
        Mockito.verify(notificationQueueService).pushRegistrationEmailToQueue(eventCaptor.capture());
        RegistrationSuccessEvent pushArg = eventCaptor.getValue();
        Assertions.assertEquals(pushArg.customerId(), generatedUserId);
        Assertions.assertEquals(pushArg.fullName(), "Lukas Maska");
        Assertions.assertEquals(pushArg.email(), "lukas@email.com");
    }

    @Test
    public void testCreate_shouldFailDueToUserConflict() {
        // prepare
        when(customerWriteRepository.saveCustomer(any(Customer.class)))
                .thenThrow(DuplicateCustomerException.class);

        // execute and verify
        assertThrows(DuplicateCustomerException.class, () -> {
            createUserUseCase.create(command);
        });
    }

    @Test
    public void testCreate_shouldSaveFailedRegistrationEvent() {
        // prepare
        final Integer generatedUserId = 10;
        when(customerWriteRepository.saveCustomer(any(Customer.class)))
                .thenReturn(generatedUserId);
        // prepare for an exception to be thrown during the email event queue push.
        Mockito.doThrow(new RuntimeException()).when(notificationQueueService)
                .pushRegistrationEmailToQueue(Mockito.any(RegistrationSuccessEvent.class));

        // execute
        final CustomerId returnedNewUserId = createUserUseCase.create(command);

        // verify
        Assertions.assertEquals(returnedNewUserId.getValue(), generatedUserId);
        Mockito.verify(customerWriteRepository, VerificationModeFactory.times(1))
                .insertFailedRegistrationEvent(any(), eq(EmailSource.Welcome));
    }
}
