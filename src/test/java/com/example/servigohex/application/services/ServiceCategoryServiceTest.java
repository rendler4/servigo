package com.example.servigohex.application.services;

import com.example.servigohex.application.ports.out.ServiceCategoryRepositoryPort;
import com.example.servigohex.domain.exceptions.DuplicateCategoryNameException;
import com.example.servigohex.domain.exceptions.ResourceNotFoundException;
import com.example.servigohex.domain.models.ServiceCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceCategoryServiceTest {

    @Mock
    private ServiceCategoryRepositoryPort serviceCategoryRepositoryPort;

    @InjectMocks
    private ServiceCategoryService serviceCategoryService;

    private ServiceCategory plomeria;

    @BeforeEach
    void setUp() {
        plomeria = ServiceCategory.builder()
                .id(1L)
                .name("Plomería")
                .description("Reparación de fugas")
                .active(true)
                .build();
    }

    @Test
    void listarActivas_delegaEnRepositorio() {
        when(serviceCategoryRepositoryPort.findAllByActiveTrue()).thenReturn(List.of(plomeria));

        List<ServiceCategory> result = serviceCategoryService.listarActivas();

        assertThat(result).containsExactly(plomeria);
    }

    @Test
    void buscarPorId_existente_devuelveCategoria() {
        when(serviceCategoryRepositoryPort.findById(1L)).thenReturn(Optional.of(plomeria));

        assertThat(serviceCategoryService.buscarPorId(1L)).isEqualTo(plomeria);
    }

    @Test
    void buscarPorId_noExistente_lanzaResourceNotFound() {
        when(serviceCategoryRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serviceCategoryService.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void crear_nombreNuevo_laGuardaActiva() {
        ServiceCategory nueva = ServiceCategory.builder().name("Pintura").active(false).build();
        when(serviceCategoryRepositoryPort.existsByName("Pintura")).thenReturn(false);
        when(serviceCategoryRepositoryPort.save(any(ServiceCategory.class))).thenAnswer(inv -> inv.getArgument(0));

        ServiceCategory result = serviceCategoryService.crear(nueva);

        assertThat(result.isActive()).isTrue();
        verify(serviceCategoryRepositoryPort).save(nueva);
    }

    @Test
    void crear_nombreDuplicado_lanzaExcepcionYNoGuarda() {
        when(serviceCategoryRepositoryPort.existsByName("Plomería")).thenReturn(true);

        assertThatThrownBy(() -> serviceCategoryService.crear(plomeria))
                .isInstanceOf(DuplicateCategoryNameException.class);

        verify(serviceCategoryRepositoryPort, never()).save(any());
    }

    @Test
    void actualizar_existente_copiaNombreYDescripcion() {
        when(serviceCategoryRepositoryPort.findById(1L)).thenReturn(Optional.of(plomeria));
        when(serviceCategoryRepositoryPort.save(any(ServiceCategory.class))).thenAnswer(inv -> inv.getArgument(0));
        ServiceCategory cambios = ServiceCategory.builder().name("Plomería y gas").description("Nueva").build();

        ServiceCategory result = serviceCategoryService.actualizar(1L, cambios);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Plomería y gas");
        assertThat(result.getDescription()).isEqualTo("Nueva");
    }

    @Test
    void actualizar_noExistente_lanzaResourceNotFound() {
        when(serviceCategoryRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serviceCategoryService.actualizar(99L, plomeria))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(serviceCategoryRepositoryPort, never()).save(any());
    }

    @Test
    void desactivar_existente_guardaConActiveFalse() {
        when(serviceCategoryRepositoryPort.findById(1L)).thenReturn(Optional.of(plomeria));

        serviceCategoryService.desactivar(1L);

        ArgumentCaptor<ServiceCategory> captor = ArgumentCaptor.forClass(ServiceCategory.class);
        verify(serviceCategoryRepositoryPort).save(captor.capture());
        assertThat(captor.getValue().isActive()).isFalse();
    }
}
