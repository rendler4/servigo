package com.example.servigohex.application.ports.out;

import com.example.servigohex.domain.models.ServiceCategory;

/**
 * Puerto de salida encargado de publicar en la cola de mensajería
 * una categoría pendiente de creación.
 */
public interface ServiceCategoryMessagePublisherPort {

    void publicarCreacion(ServiceCategory categoria);
}
