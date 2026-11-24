package com.example.servigohex.infrastructure.adapters;

import com.example.servigohex.application.ports.in.ServiceCategoryUseCase;
import com.example.servigohex.domain.models.ServiceCategory;
import com.example.servigohex.infrastructure.adapters.in.messaging.ServiceCategoryCreationConsumer;
import com.example.servigohex.infrastructure.adapters.out.messaging.RabbitServiceCategoryPublisherAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessagingAdaptersTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ServiceCategoryUseCase serviceCategoryUseCase;

    private final ServiceCategory pintura = ServiceCategory.builder().name("Pintura").build();

    @Test
    void publisher_enviaAlExchangeConLaRoutingKeyConfigurada() {
        RabbitServiceCategoryPublisherAdapter publisher =
                new RabbitServiceCategoryPublisherAdapter(rabbitTemplate, "servigo.exchange", "categorias.creacion");

        publisher.publicarCreacion(pintura);

        verify(rabbitTemplate).convertAndSend("servigo.exchange", "categorias.creacion", pintura);
    }

    @Test
    void consumer_delegaEnElMismoCasoDeUsoQueElRest() {
        ServiceCategoryCreationConsumer consumer = new ServiceCategoryCreationConsumer(serviceCategoryUseCase);

        consumer.procesarCreacion(pintura);

        verify(serviceCategoryUseCase).crear(pintura);
    }
}
