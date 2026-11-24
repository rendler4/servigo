package com.example.servigohex.infrastructure.adapters.out.messaging;

import com.example.servigohex.application.ports.out.ServiceCategoryMessagePublisherPort;
import com.example.servigohex.domain.models.ServiceCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida (sender) que publica en RabbitMQ la categoría
 * para que sea procesada de forma asíncrona por el consumer.
 */
@Component
public class RabbitServiceCategoryPublisherAdapter implements ServiceCategoryMessagePublisherPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitServiceCategoryPublisherAdapter.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public RabbitServiceCategoryPublisherAdapter(RabbitTemplate rabbitTemplate,
            @Value("${app.messaging.rabbitmq.exchange}") String exchange,
            @Value("${app.messaging.rabbitmq.categorias.routing-key}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    @Override
    public void publicarCreacion(ServiceCategory categoria) {
        log.info("Publicando en la cola la creación de la categoría: {}", categoria.getName());
        rabbitTemplate.convertAndSend(exchange, routingKey, categoria);
    }
}
