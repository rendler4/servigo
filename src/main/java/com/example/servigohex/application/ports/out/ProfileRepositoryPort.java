package com.example.servigohex.application.ports.out;

import com.example.servigohex.domain.models.Profile;

import java.util.Optional;

public interface ProfileRepositoryPort {

    Optional<Profile> findByEmail(String email);

    Optional<Profile> findById(Long id);

    Profile save(Profile profile);

    boolean existsByEmail(String email);
}
