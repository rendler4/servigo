package com.example.servigohex.application.services;

import com.example.servigohex.application.ports.in.ReporteProveedoresPendientesUseCase;
import com.example.servigohex.domain.models.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReporteProveedoresPendientesService implements ReporteProveedoresPendientesUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReporteProveedoresPendientesService.class);

    @Override
    public void reportarPendiente(Profile proveedor) {
        // Punto de extensión: aquí se podría persistir el reporte o notificar
        // al proveedor para que cargue su documento. Por ahora solo se registra el log.
        log.info("Proveedor sin identidad aprobada: {} - {} (estado {})",
                proveedor.getEmail(), proveedor.getFullName(), proveedor.getIdentityStatus());
    }
}
