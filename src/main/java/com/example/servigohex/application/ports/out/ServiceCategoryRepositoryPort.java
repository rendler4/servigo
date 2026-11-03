package com.example.servigohex.application.ports.out;

import com.example.servigohex.domain.models.ServiceCategory;

import java.util.List;
import java.util.Optional;

public interface ServiceCategoryRepositoryPort {

    List<ServiceCategory> findAllByActiveTrue();

    Optional<ServiceCategory> findById(Long id);

    ServiceCategory save(ServiceCategory categoria);

    boolean existsByName(String name);
}
