package com.apiscan.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ queue, exchange, and binding configuration.
 */
@Configuration
public class RabbitMQConfig {

    public static final String SCAN_QUEUE = "scan.queue";
    public static final String SCAN_EXCHANGE = "scan.exchange";
    public static final String SCAN_ROUTING_KEY = "scan.#";
    public static final String SCAN_DEAD_QUEUE = "scan.dead.queue";

    @Bean
    public Queue scanQueue() {
        return QueueBuilder.durable(SCAN_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", SCAN_DEAD_QUEUE)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(SCAN_DEAD_QUEUE).build();
    }

    @Bean
    public TopicExchange scanExchange() {
        return new TopicExchange(SCAN_EXCHANGE);
    }

    @Bean
    public Binding scanBinding(Queue scanQueue, TopicExchange scanExchange) {
        return BindingBuilder.bind(scanQueue).to(scanExchange).with(SCAN_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
