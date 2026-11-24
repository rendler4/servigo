package com.example.servigohex.application.services;

import com.example.servigohex.application.ports.in.ReporteCategoriasUseCase;
import com.example.servigohex.application.ports.out.ServiceCategoryRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ReporteCategoriasService implements ReporteCategoriasUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReporteCategoriasService.class);

    private final ServiceCategoryRepositoryPort serviceCategoryRepositoryPort;

    public ReporteCategoriasService(ServiceCategoryRepositoryPort serviceCategoryRepositoryPort) {
        this.serviceCategoryRepositoryPort = serviceCategoryRepositoryPort;
    }

    @Override
    public void reportarEstadoCatalogo(LocalDate fechaCorte) {
        long activas = serviceCategoryRepositoryPort.countByActive(true);
        long inactivas = serviceCategoryRepositoryPort.countByActive(false);
        log.info("Catálogo de categorías al {}: {} activas, {} inactivas", fechaCorte, activas, inactivas);
    }
}
