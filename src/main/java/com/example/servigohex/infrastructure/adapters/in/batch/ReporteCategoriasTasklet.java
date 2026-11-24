package com.example.servigohex.infrastructure.adapters.in.batch;

import com.example.servigohex.application.ports.in.ReporteCategoriasUseCase;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Adaptador de entrada ("in") para Spring Batch: delega la lógica de negocio
 * en {@link ReporteCategoriasUseCase}, sin conocer detalles de persistencia.
 *
 * Al ser un @Component con @StepScope, Spring resuelve los jobParameters
 * en cada ejecución del step.
 */
@Component
@StepScope
public class ReporteCategoriasTasklet implements Tasklet {

    private final ReporteCategoriasUseCase reporteCategoriasUseCase;
    private final String fechaCorte;

    public ReporteCategoriasTasklet(
            ReporteCategoriasUseCase reporteCategoriasUseCase,
            @Value("#{jobParameters['fechaCorte']}") String fechaCorte) {
        this.reporteCategoriasUseCase = reporteCategoriasUseCase;
        this.fechaCorte = fechaCorte;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        reporteCategoriasUseCase.reportarEstadoCatalogo(LocalDate.parse(fechaCorte));
        return RepeatStatus.FINISHED;
    }
}
