package com.example.servigohex.infrastructure.config;

import com.example.servigohex.domain.models.Profile;
import com.example.servigohex.infrastructure.adapters.in.batch.ProveedorPendienteItemProcessor;
import com.example.servigohex.infrastructure.adapters.in.batch.ProveedorPendienteItemReader;
import com.example.servigohex.infrastructure.adapters.in.batch.ProveedorPendienteItemWriter;
import com.example.servigohex.infrastructure.adapters.out.persistence.entities.ProfileEntity;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Job chunk-oriented: reporta los proveedores cuya identidad aún no está
 * aprobada (not_submitted, pending o rejected). No modifica datos.
 *
 * A diferencia de {@link ReporteCategoriasBatchConfig} (un Tasklet), este
 * Step lee, procesa y escribe en lotes, con un adaptador "in" por etapa.
 */
@Configuration
public class ReporteProveedoresPendientesBatchConfig {

    private static final int CHUNK_SIZE = 5;

    @Bean
    public Job reporteProveedoresPendientesJob(JobRepository jobRepository,
                                               Step reporteProveedoresPendientesStep) {
        return new JobBuilder("reporteProveedoresPendientesJob", jobRepository)
                .start(reporteProveedoresPendientesStep)
                .build();
    }

    @Bean
    public Step reporteProveedoresPendientesStep(JobRepository jobRepository,
                                                 PlatformTransactionManager transactionManager,
                                                 ProveedorPendienteItemReader reader,
                                                 ProveedorPendienteItemProcessor processor,
                                                 ProveedorPendienteItemWriter writer) {
        return new StepBuilder("reporteProveedoresPendientesStep", jobRepository)
                .<ProfileEntity, Profile>chunk(CHUNK_SIZE, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
