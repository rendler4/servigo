package com.example.servigohex.infrastructure.adapters.in.batch;

import com.example.servigohex.application.ports.in.ReporteProveedoresPendientesUseCase;
import com.example.servigohex.domain.models.Profile;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

/**
 * Adaptador de entrada ("in") del step chunk-oriented: por cada proveedor
 * del chunk delega la lógica de negocio en {@link ReporteProveedoresPendientesUseCase}.
 */
@Component
public class ProveedorPendienteItemWriter implements ItemWriter<Profile> {

    private final ReporteProveedoresPendientesUseCase reporteProveedoresPendientesUseCase;

    public ProveedorPendienteItemWriter(ReporteProveedoresPendientesUseCase reporteProveedoresPendientesUseCase) {
        this.reporteProveedoresPendientesUseCase = reporteProveedoresPendientesUseCase;
    }

    @Override
    public void write(Chunk<? extends Profile> chunk) {
        for (Profile proveedor : chunk) {
            reporteProveedoresPendientesUseCase.reportarPendiente(proveedor);
        }
    }
}
