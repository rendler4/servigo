package com.example.servigohex.application.ports.in;

import com.example.servigohex.domain.models.ServiceCategory;

/**
 * Caso de uso que permite crear una categoría de forma asíncrona,
 * enviando la solicitud a una cola de mensajería en lugar de persistirla
 * directamente. La creación real la realiza el consumer correspondiente.
 */
public interface ServiceCategoryMessagingUseCase {

    void enviarCreacion(ServiceCategory categoria);
}
