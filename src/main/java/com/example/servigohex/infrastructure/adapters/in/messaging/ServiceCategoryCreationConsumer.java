package com.example.servigohex.infrastructure.adapters.in.messaging;

import com.example.servigohex.application.ports.in.ServiceCategoryUseCase;
import com.example.servigohex.domain.models.ServiceCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Adaptador de entrada (consumer) que escucha la cola de creación de
 * categorías y delega en el mismo caso de uso que utiliza el controlador
 * REST, de modo que se aplican las mismas reglas (nombre único, cache).
 */
@Component
public class ServiceCategoryCreationConsumer {

    private static final Logger log = LoggerFactory.getLogger(ServiceCategoryCreationConsumer.class);

    private final ServiceCategoryUseCase serviceCategoryUseCase;

    public ServiceCategoryCreationConsumer(ServiceCategoryUseCase serviceCategoryUseCase) {
        this.serviceCategoryUseCase = serviceCategoryUseCase;
    }

    @RabbitListener(queues = "${app.messaging.rabbitmq.categorias.queue}")
    public void procesarCreacion(ServiceCategory categoria) {
        log.info("Mensaje recibido de la cola para crear la categoría: {}", categoria.getName());
        serviceCategoryUseCase.crear(categoria);
    }
}
