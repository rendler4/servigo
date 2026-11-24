package com.example.servigohex.infrastructure.adapters.in.batch;

import com.example.servigohex.infrastructure.adapters.out.persistence.entities.ProfileEntity;
import com.example.servigohex.infrastructure.adapters.out.persistence.repositories.ProfileJpaRepository;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Adaptador de entrada ("in") del step chunk-oriented: pagina los
 * proveedores sin identidad aprobada directamente desde el JPA repository,
 * sin contener lógica de negocio.
 */
@Component
@StepScope
public class ProveedorPendienteItemReader extends RepositoryItemReader<ProfileEntity> {

    static final int PAGE_SIZE = 5;

    public ProveedorPendienteItemReader(ProfileJpaRepository profileJpaRepository) {
        setRepository(profileJpaRepository);
        setMethodName("findProveedoresSinIdentidadAprobada");
        setPageSize(PAGE_SIZE);
        setSort(Map.of("id", Sort.Direction.ASC));
    }
}
