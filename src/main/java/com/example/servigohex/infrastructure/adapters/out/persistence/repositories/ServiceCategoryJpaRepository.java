package com.example.servigohex.infrastructure.adapters.out.persistence.repositories;

import com.example.servigohex.infrastructure.adapters.out.persistence.entities.ServiceCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceCategoryJpaRepository extends JpaRepository<ServiceCategoryEntity, Long> {

    List<ServiceCategoryEntity> findAllByActiveTrue();

    boolean existsByName(String name);
}
