package com.example.servigohex.infrastructure.adapters.out.persistence.repositories;

import com.example.servigohex.infrastructure.adapters.out.persistence.entities.ProfileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, Long> {

    Optional<ProfileEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    // Usado por el job batch de proveedores pendientes de validar identidad.
    @Query("""
            SELECT p FROM ProfileEntity p
            WHERE p.userType = com.example.servigohex.domain.enums.UserType.PROVIDER
              AND p.identityStatus <> com.example.servigohex.domain.enums.IdentityStatus.APPROVED
            """)
    Page<ProfileEntity> findProveedoresSinIdentidadAprobada(Pageable pageable);
}
