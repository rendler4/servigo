package com.example.servigohex.infrastructure.adapters.out.persistence.repositories;

import com.example.servigohex.infrastructure.adapters.out.persistence.entities.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, Long> {

    Optional<ProfileEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
