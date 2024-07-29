package com.fooddelivery.commerce.infrastructure.service.queue;

import com.fooddelivery.commerce.domain.order.event.OrderStatusChangeEvent;
import com.fooddelivery.commerce.infrastructure.exception.rabbit.ConnectionFailedException;
import com.fooddelivery.commerce.infrastructure.exception.rabbit.EventSendFailedException;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class RabbitOrderProducer  {
    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange orderStatusExchange;

    @Autowired
    public RabbitOrderProducer(
            RabbitTemplate rabbitTemplate,
            Binding createdOrderBinding,
            TopicExchange orderStatusExchange
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.orderStatusExchange = orderStatusExchange;
    }

    @Retryable(
            maxAttempts = 3,
            include = ConnectionFailedException.class,
            backoff = @Backoff(delay = 500)
    )
    public void pushOrderChangedEvent(String status, OrderStatusChangeEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    orderStatusExchange.getName(),
                    this.getExchangeAndRoutingKey(status),
                    event
            );
        } catch (AmqpConnectException e) {
            throw new ConnectionFailedException(e.getMessage());
        } catch (Exception e) {
            throw new EventSendFailedException(e.getMessage());
        }
    }


    private String getExchangeAndRoutingKey(String status) {
        if ("created".equals(status)) {
            return "created-routing-key";
        } else if ("delivering".equals(status)) {
            return "delivering-routing-key";
        } else if ("pending".equals(status)) {
            return "pending-routing-key";
        } else {
            throw new IllegalArgumentException("Unknown order status: " + status);
        }
    }
}