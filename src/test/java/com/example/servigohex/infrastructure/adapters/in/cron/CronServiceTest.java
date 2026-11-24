package com.example.servigohex.infrastructure.adapters.in.cron;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CronServiceTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job reporteCategoriasJob;

    @Mock
    private Job reporteProveedoresPendientesJob;

    @Test
    void ejecutarReportes_lanzaAmbosJobsConFechaDeCorteYEjecucionUnica() throws Exception {
        CronService cronService = new CronService(jobLauncher, reporteCategoriasJob,
                reporteProveedoresPendientesJob, "America/Bogota");

        cronService.ejecutarReportes();

        ArgumentCaptor<JobParameters> categorias = ArgumentCaptor.forClass(JobParameters.class);
        ArgumentCaptor<JobParameters> proveedores = ArgumentCaptor.forClass(JobParameters.class);
        verify(jobLauncher).run(org.mockito.ArgumentMatchers.eq(reporteCategoriasJob), categorias.capture());
        verify(jobLauncher).run(org.mockito.ArgumentMatchers.eq(reporteProveedoresPendientesJob), proveedores.capture());

        String hoy = LocalDate.now(ZoneId.of("America/Bogota")).toString();
        assertThat(categorias.getValue().getString("fechaCorte")).isEqualTo(hoy);
        assertThat(categorias.getValue().getString("ejecucionId"))
                .isNotEqualTo(proveedores.getValue().getString("ejecucionId"));
    }
}
