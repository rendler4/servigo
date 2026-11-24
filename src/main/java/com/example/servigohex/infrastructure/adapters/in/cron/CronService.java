package com.example.servigohex.infrastructure.adapters.in.cron;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

/**
 * Dispara periódicamente los jobs de Spring Batch (reporte del catálogo de
 * categorías y reporte de proveedores pendientes de validar identidad).
 * El cron "-" (valor por defecto) desactiva la tarea.
 */
@Service
public class CronService {

    private final JobLauncher jobLauncher;
    private final Job reporteCategoriasJob;
    private final Job reporteProveedoresPendientesJob;
    private final ZoneId zona;

    public CronService(JobLauncher jobLauncher,
                       @Qualifier("reporteCategoriasJob") Job reporteCategoriasJob,
                       @Qualifier("reporteProveedoresPendientesJob") Job reporteProveedoresPendientesJob,
                       @Value("${app.batch.zona:UTC}") String zona) {
        this.jobLauncher = jobLauncher;
        this.reporteCategoriasJob = reporteCategoriasJob;
        this.reporteProveedoresPendientesJob = reporteProveedoresPendientesJob;
        this.zona = ZoneId.of(zona);
    }

    @Scheduled(cron = "${app.batch.cron:-}", zone = "${app.batch.zona:UTC}")
    public void ejecutarReportes() throws Exception {
        jobLauncher.run(reporteCategoriasJob, nuevosParametros());
        jobLauncher.run(reporteProveedoresPendientesJob, nuevosParametros());
    }

    // ejecucionId único: Spring Batch no relanza un job con parámetros ya completados.
    private JobParameters nuevosParametros() {
        return new JobParametersBuilder()
                .addString("fechaCorte", LocalDate.now(zona).toString())
                .addString("ejecucionId", UUID.randomUUID().toString())
                .toJobParameters();
    }
}
