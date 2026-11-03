package com.example.servigohex.infrastructure.adapters.out.persistence;

import com.example.servigohex.application.ports.out.ServiceCategoryRepositoryPort;
import com.example.servigohex.domain.models.ServiceCategory;
import com.example.servigohex.infrastructure.adapters.out.persistence.entities.ServiceCategoryEntity;
import com.example.servigohex.infrastructure.adapters.out.persistence.mappers.ServiceCategoryMapper;
import com.example.servigohex.infrastructure.adapters.out.persistence.repositories.ServiceCategoryJpaRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ServiceCategoryPersistenceAdapter implements ServiceCategoryRepositoryPort {

    private final ServiceCategoryJpaRepository serviceCategoryJpaRepository;

    public ServiceCategoryPersistenceAdapter(ServiceCategoryJpaRepository serviceCategoryJpaRepository) {
        this.serviceCategoryJpaRepository = serviceCategoryJpaRepository;
    }

    @Override
    public List<ServiceCategory> findAllByActiveTrue() {
        // ArrayList (no .toList()): el cache de Redis serializa con tipado polimorfico
        // (RedisCacheConfig) y los tipos inmutables de .toList()/List.of() no deserializan
        // bien de vuelta, sobre todo cuando la lista queda vacia.
        return serviceCategoryJpaRepository.findAllByActiveTrue().stream()
                .map(ServiceCategoryMapper.INSTANCE::toServiceCategory)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Optional<ServiceCategory> findById(Long id) {
        return serviceCategoryJpaRepository.findById(id).map(ServiceCategoryMapper.INSTANCE::toServiceCategory);
    }

    @Override
    public ServiceCategory save(ServiceCategory categoria) {
        ServiceCategoryEntity entity = ServiceCategoryMapper.INSTANCE.toEntity(categoria);
        return ServiceCategoryMapper.INSTANCE.toServiceCategory(serviceCategoryJpaRepository.save(entity));
    }

    @Override
    public boolean existsByName(String name) {
        return serviceCategoryJpaRepository.existsByName(name);
    }
}
