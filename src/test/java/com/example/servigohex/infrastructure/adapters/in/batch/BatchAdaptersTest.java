package com.example.servigohex.infrastructure.adapters.in.batch;

import com.example.servigohex.application.ports.in.ReporteCategoriasUseCase;
import com.example.servigohex.application.ports.in.ReporteProveedoresPendientesUseCase;
import com.example.servigohex.domain.enums.IdentityStatus;
import com.example.servigohex.domain.enums.UserType;
import com.example.servigohex.domain.models.Profile;
import com.example.servigohex.infrastructure.adapters.out.persistence.entities.ProfileEntity;
import com.example.servigohex.infrastructure.adapters.out.persistence.repositories.ProfileJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BatchAdaptersTest {

    @Mock
    private ReporteCategoriasUseCase reporteCategoriasUseCase;

    @Mock
    private ReporteProveedoresPendientesUseCase reporteProveedoresPendientesUseCase;

    @Mock
    private ProfileJpaRepository profileJpaRepository;

    private ProfileEntity proveedorPendiente() {
        ProfileEntity entity = new ProfileEntity();
        entity.setId(2L);
        entity.setEmail("luis@servigo.co");
        entity.setFullName("Luis Pérez");
        entity.setUserType(UserType.PROVIDER);
        entity.setIdentityStatus(IdentityStatus.PENDING);
        return entity;
    }

    @Test
    void tasklet_parseaLaFechaDeCorteYDelegaEnElCasoDeUso() {
        ReporteCategoriasTasklet tasklet = new ReporteCategoriasTasklet(reporteCategoriasUseCase, "2026-11-24");

        RepeatStatus status = tasklet.execute(null, null);

        assertThat(status).isEqualTo(RepeatStatus.FINISHED);
        verify(reporteCategoriasUseCase).reportarEstadoCatalogo(LocalDate.of(2026, 11, 24));
    }

    @Test
    void reader_leeLosProveedoresPendientesDesdeElRepositorio() throws Exception {
        when(profileJpaRepository.findProveedoresSinIdentidadAprobada(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(proveedorPendiente())));
        ProveedorPendienteItemReader reader = new ProveedorPendienteItemReader(profileJpaRepository);

        ProfileEntity leido = reader.read();

        assertThat(leido).isNotNull();
        assertThat(leido.getEmail()).isEqualTo("luis@servigo.co");
    }

    @Test
    void processor_convierteLaEntidadAlModeloDeDominio() {
        Profile profile = new ProveedorPendienteItemProcessor().process(proveedorPendiente());

        assertThat(profile.getEmail()).isEqualTo("luis@servigo.co");
        assertThat(profile.getIdentityStatus()).isEqualTo(IdentityStatus.PENDING);
    }

    @Test
    void writer_reportaCadaProveedorDelChunk() {
        Profile luis = Profile.builder().email("luis@servigo.co").build();
        Profile marta = Profile.builder().email("marta@servigo.co").build();
        ProveedorPendienteItemWriter writer = new ProveedorPendienteItemWriter(reporteProveedoresPendientesUseCase);

        writer.write(Chunk.of(luis, marta));

        verify(reporteProveedoresPendientesUseCase).reportarPendiente(luis);
        verify(reporteProveedoresPendientesUseCase).reportarPendiente(marta);
    }
}
