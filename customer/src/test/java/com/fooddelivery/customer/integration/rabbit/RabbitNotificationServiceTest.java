package com.fooddelivery.customer.integration.rabbit;


import com.fooddelivery.customer.application.event.RegistrationSuccessEvent;
import com.fooddelivery.customer.infrastructure.exception.http.ConnectionFailedException;
import com.fooddelivery.customer.infrastructure.exception.http.MessageSendFailedException;
import com.fooddelivery.customer.infrastructure.service.queue.RabbitNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitNotificationServiceTest {
    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitNotificationService notificationService;

    @Test
    public void testPushSuccessEmailToQueue_shouldRetryDueToFail() {
        Mockito.doThrow(new AmqpConnectException(new Exception("Exception 1")))
                .doThrow(new AmqpConnectException(new Exception("Exception 2")))
                .doThrow(new AmqpConnectException(new Exception("Exception 3")))
                .when(rabbitTemplate)
                .convertAndSend(anyString(), anyString(),any(Object.class));

        assertThrows(ConnectionFailedException.class, () -> {
            notificationService.pushRegistrationEmailToQueue(
                    new RegistrationSuccessEvent(1, "Lukas Maska", "lukas@email.com")
            );
        });

        Mockito.verify(rabbitTemplate, VerificationModeFactory.times(3))
                .convertAndSend(anyString(), anyString(),any(Object.class));
    }

    @Test
    public void testPushSuccessEmailToQueue_shouldFailAndNotRetry() {
        Mockito.doThrow(new RuntimeException("message"))
                .when(rabbitTemplate)
                .convertAndSend(anyString(), anyString(),any(Object.class));

        assertThrows(MessageSendFailedException.class, () -> {
            notificationService.pushRegistrationEmailToQueue(
                    new RegistrationSuccessEvent(1, "Lukas Maska", "lukas@email.com")
            );
        });

        Mockito.verify(rabbitTemplate, VerificationModeFactory.times(1))
                .convertAndSend(anyString(), anyString(),any(Object.class));
    }

    @Test
    public void testPushSuccessEmailToQueue_shouldSuccess() {
        assertDoesNotThrow(() -> {
            notificationService.pushRegistrationEmailToQueue(
                    new RegistrationSuccessEvent(1, "Lukas Maska", "lukas@email.com")
            );
        });

        Mockito.verify(rabbitTemplate, VerificationModeFactory.times(1))
                .convertAndSend(anyString(), anyString(),any(Object.class));
    }
}
