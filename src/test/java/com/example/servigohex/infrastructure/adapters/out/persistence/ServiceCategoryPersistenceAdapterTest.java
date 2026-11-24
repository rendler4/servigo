package com.example.servigohex.infrastructure.adapters.out.persistence;

import com.example.servigohex.domain.models.ServiceCategory;
import com.example.servigohex.infrastructure.adapters.out.persistence.entities.ServiceCategoryEntity;
import com.example.servigohex.infrastructure.adapters.out.persistence.repositories.ServiceCategoryJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceCategoryPersistenceAdapterTest {

    @Mock
    private ServiceCategoryJpaRepository serviceCategoryJpaRepository;

    @InjectMocks
    private ServiceCategoryPersistenceAdapter adapter;

    private ServiceCategoryEntity entity;

    @BeforeEach
    void setUp() {
        entity = new ServiceCategoryEntity(1L, "Plomería", "Reparación de fugas", true);
    }

    @Test
    void findAllByActiveTrue_devuelveListaMutableParaElCacheDeRedis() {
        when(serviceCategoryJpaRepository.findAllByActiveTrue()).thenReturn(List.of(entity));

        List<ServiceCategory> result = adapter.findAllByActiveTrue();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Plomería");
        assertThat(result).isInstanceOf(ArrayList.class);
    }

    @Test
    void findById_existente_mapeaCategoria() {
        when(serviceCategoryJpaRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertThat(adapter.findById(1L)).map(ServiceCategory::getName).contains("Plomería");
    }

    @Test
    void save_devuelveCategoriaGuardada() {
        when(serviceCategoryJpaRepository.save(any(ServiceCategoryEntity.class))).thenReturn(entity);

        ServiceCategory result = adapter.save(ServiceCategory.builder().name("Plomería").active(true).build());

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.isActive()).isTrue();
    }

    @Test
    void existsByName_delegaEnRepositorio() {
        when(serviceCategoryJpaRepository.existsByName("Plomería")).thenReturn(true);

        assertThat(adapter.existsByName("Plomería")).isTrue();
    }

    @Test
    void countByActive_delegaEnRepositorio() {
        when(serviceCategoryJpaRepository.countByActive(false)).thenReturn(3L);

        assertThat(adapter.countByActive(false)).isEqualTo(3L);
    }
}
