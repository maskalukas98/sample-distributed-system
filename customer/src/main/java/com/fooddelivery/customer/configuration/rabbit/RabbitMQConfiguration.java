package com.fooddelivery.customer.configuration.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

@Configuration
public class RabbitMQConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "spring.rabbitmq")
    public RabbitMQProperties rabbitMQProperties() {
        return new RabbitMQProperties();
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        RabbitMQProperties props = rabbitMQProperties();
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(props.getHost());
        connectionFactory.setPort(props.getPort());
        connectionFactory.setUsername(props.getUsername());
        connectionFactory.setPassword(props.getPassword());
        connectionFactory.setVirtualHost(props.getVirtualHost());

        return connectionFactory;
    }

    @Bean
    public TopicExchange eventTopicExchange() {
        return new TopicExchange("email_exchange");
    }

    @Bean
    public Queue emailRegistrationSuccessQueue() {
        return new Queue("email_registration_success_queue");
    }

    @Bean
    @Qualifier("registrationEmailBinding")
    public Binding registrationEmailBinding(TopicExchange eventTopicExchange, Queue emailSuccessQueue) {
        return BindingBuilder.bind(emailSuccessQueue).to(eventTopicExchange).with("registration_success_event");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
