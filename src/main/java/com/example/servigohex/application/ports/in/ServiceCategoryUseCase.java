package com.example.servigohex.application.ports.in;

import com.example.servigohex.domain.models.ServiceCategory;

import java.util.List;

public interface ServiceCategoryUseCase {

    List<ServiceCategory> listarActivas();

    ServiceCategory buscarPorId(Long id);

    ServiceCategory crear(ServiceCategory categoria);

    ServiceCategory actualizar(Long id, ServiceCategory categoria);

    void desactivar(Long id);
}
