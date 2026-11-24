package com.example.servigohex.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la infraestructura de mensajería (RabbitMQ) usada
 * como canal alterno de entrada para crear categorías.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${app.messaging.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.messaging.rabbitmq.categorias.queue}")
    private String categoriasQueueName;

    @Value("${app.messaging.rabbitmq.categorias.routing-key}")
    private String categoriasRoutingKey;

    @Bean
    public DirectExchange servigoExchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    public Queue categoriasCreacionQueue() {
        return new Queue(categoriasQueueName, true);
    }

    @Bean
    public Binding categoriasCreacionBinding(Queue categoriasCreacionQueue, DirectExchange servigoExchange) {
        return BindingBuilder.bind(categoriasCreacionQueue)
                .to(servigoExchange)
                .with(categoriasRoutingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
