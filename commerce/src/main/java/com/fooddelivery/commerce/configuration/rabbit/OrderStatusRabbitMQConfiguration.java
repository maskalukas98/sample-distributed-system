package com.fooddelivery.commerce.configuration.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

@Configuration
public class OrderStatusRabbitMQConfiguration {
    @Bean
    public TopicExchange orderStatusExchange() {
        return new TopicExchange("order-status-exchange");
    }

    @Bean
    public Queue createdOrderQueue() {
        return new Queue("created-order-queue");
    }

    @Bean
    public Queue deliveringOrderQueue() {
        return new Queue("delivering-order-queue");
    }


    @Bean
    public Queue cancelledOrderQueue() {
        return new Queue("cancelled-order-queue");
    }

    @Bean
    public Binding createdOrderBinding() {
        return BindingBuilder.bind(createdOrderQueue()).to(orderStatusExchange()).with("order.status.created");
    }

    @Bean
    public Binding deliveringOrderBinding() {
        return BindingBuilder.bind(deliveringOrderQueue()).to(orderStatusExchange()).with("order.status.delivering");
    }

    @Bean
    public Binding cancelledOrderBinding() {
        return BindingBuilder.bind(cancelledOrderQueue()).to(orderStatusExchange()).with("order.status.cancelled");
    }
}
