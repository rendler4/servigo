package com.example.servigohex.application.services;

import com.example.servigohex.application.ports.out.ServiceCategoryMessagePublisherPort;
import com.example.servigohex.application.ports.out.ServiceCategoryRepositoryPort;
import com.example.servigohex.domain.enums.IdentityStatus;
import com.example.servigohex.domain.enums.UserType;
import com.example.servigohex.domain.models.Profile;
import com.example.servigohex.domain.models.ServiceCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessagingAndReportServicesTest {

    @Mock
    private ServiceCategoryMessagePublisherPort publisherPort;

    @Mock
    private ServiceCategoryRepositoryPort serviceCategoryRepositoryPort;

    @Test
    void enviarCreacion_publicaLaCategoriaEnLaCola() {
        ServiceCategoryMessagingService service = new ServiceCategoryMessagingService(publisherPort);
        ServiceCategory categoria = ServiceCategory.builder().name("Pintura").build();

        service.enviarCreacion(categoria);

        verify(publisherPort).publicarCreacion(categoria);
    }

    @Test
    void reportarEstadoCatalogo_cuentaActivasEInactivas() {
        ReporteCategoriasService service = new ReporteCategoriasService(serviceCategoryRepositoryPort);
        when(serviceCategoryRepositoryPort.countByActive(true)).thenReturn(8L);
        when(serviceCategoryRepositoryPort.countByActive(false)).thenReturn(2L);

        service.reportarEstadoCatalogo(LocalDate.of(2026, 11, 24));

        verify(serviceCategoryRepositoryPort).countByActive(true);
        verify(serviceCategoryRepositoryPort).countByActive(false);
    }

    @Test
    void reportarPendiente_registraElProveedorSinFallar() {
        ReporteProveedoresPendientesService service = new ReporteProveedoresPendientesService();
        Profile proveedor = Profile.builder()
                .email("luis@servigo.co")
                .fullName("Luis Pérez")
                .userType(UserType.PROVIDER)
                .identityStatus(IdentityStatus.PENDING)
                .build();

        assertThatCode(() -> service.reportarPendiente(proveedor)).doesNotThrowAnyException();
    }
}
