package com.example.servigohex.infrastructure.config;

import com.example.servigohex.infrastructure.adapters.in.batch.ReporteCategoriasTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Job de tipo Tasklet: reporta cuántas categorías del catálogo están activas
 * e inactivas a la fecha de corte. No modifica datos.
 *
 * Solo orquesta Job/Step; el paso lo ejecuta {@link ReporteCategoriasTasklet},
 * que delega en el caso de uso (infrastructure -> application -> domain).
 */
@Configuration
public class ReporteCategoriasBatchConfig {

    @Bean
    public Job reporteCategoriasJob(JobRepository jobRepository, Step reporteCategoriasStep) {
        return new JobBuilder("reporteCategoriasJob", jobRepository)
                .start(reporteCategoriasStep)
                .build();
    }

    @Bean
    public Step reporteCategoriasStep(JobRepository jobRepository,
                                      PlatformTransactionManager transactionManager,
                                      ReporteCategoriasTasklet reporteCategoriasTasklet) {
        return new StepBuilder("reporteCategoriasStep", jobRepository)
                .tasklet(reporteCategoriasTasklet, transactionManager)
                .build();
    }
}
