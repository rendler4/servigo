package com.example.servigohex.application.services;

import com.example.servigohex.application.ports.in.ServiceCategoryMessagingUseCase;
import com.example.servigohex.application.ports.out.ServiceCategoryMessagePublisherPort;
import com.example.servigohex.domain.models.ServiceCategory;
import org.springframework.stereotype.Service;

@Service
public class ServiceCategoryMessagingService implements ServiceCategoryMessagingUseCase {

    private final ServiceCategoryMessagePublisherPort serviceCategoryMessagePublisherPort;

    public ServiceCategoryMessagingService(ServiceCategoryMessagePublisherPort serviceCategoryMessagePublisherPort) {
        this.serviceCategoryMessagePublisherPort = serviceCategoryMessagePublisherPort;
    }

    @Override
    public void enviarCreacion(ServiceCategory categoria) {
        // aquí se podrían aplicar validaciones previas al envío a la cola
        serviceCategoryMessagePublisherPort.publicarCreacion(categoria);
    }
}
