package com.fooddelivery.customer.infrastructure.service.queue;

import com.fooddelivery.customer.application.event.RegistrationSuccessEvent;
import com.fooddelivery.customer.domain.port.output.NotificationQueuePort;
import com.fooddelivery.customer.infrastructure.exception.http.ConnectionFailedException;
import com.fooddelivery.customer.infrastructure.exception.http.MessageSendFailedException;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class RabbitNotificationService implements NotificationQueuePort {
    private final RabbitTemplate rabbitTemplate;
    private final Binding registrationEmailBinding;
    private final TopicExchange emailExchange;

    @Autowired
    public RabbitNotificationService(
            RabbitTemplate rabbitTemplate,
            Binding registrationEmailBinding,
            TopicExchange emailExchange
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.registrationEmailBinding = registrationEmailBinding;
        this.emailExchange = emailExchange;
    }

    @Override
    @Retryable(
            maxAttempts = 3,
            include = ConnectionFailedException.class,
            backoff = @Backoff(delay = 500)
    )
    public void pushRegistrationEmailToQueue(RegistrationSuccessEvent event)
            throws ConnectionFailedException, MessageSendFailedException {
        try {
            rabbitTemplate.convertAndSend(
                    emailExchange.getName(),
                    registrationEmailBinding.getRoutingKey(),
                    event
            );
        } catch (AmqpConnectException e) {
            throw new ConnectionFailedException(e.getMessage());
        } catch (Exception e) {
            throw new MessageSendFailedException(e.getMessage());
        }
    }
}
