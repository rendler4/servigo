package com.example.servigohex.infrastructure.adapters.out.persistence;

import com.example.servigohex.application.ports.out.ProfileRepositoryPort;
import com.example.servigohex.domain.models.Profile;
import com.example.servigohex.infrastructure.adapters.out.persistence.entities.ProfileEntity;
import com.example.servigohex.infrastructure.adapters.out.persistence.mappers.ProfileMapper;
import com.example.servigohex.infrastructure.adapters.out.persistence.repositories.ProfileJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProfilePersistenceAdapter implements ProfileRepositoryPort {

    private final ProfileJpaRepository profileJpaRepository;

    public ProfilePersistenceAdapter(ProfileJpaRepository profileJpaRepository) {
        this.profileJpaRepository = profileJpaRepository;
    }

    @Override
    public Optional<Profile> findByEmail(String email) {
        return profileJpaRepository.findByEmail(email).map(ProfileMapper.INSTANCE::toProfile);
    }

    @Override
    public Optional<Profile> findById(Long id) {
        return profileJpaRepository.findById(id).map(ProfileMapper.INSTANCE::toProfile);
    }

    @Override
    public Profile save(Profile profile) {
        ProfileEntity entity = ProfileMapper.INSTANCE.toEntity(profile);
        return ProfileMapper.INSTANCE.toProfile(profileJpaRepository.save(entity));
    }

    @Override
    public boolean existsByEmail(String email) {
        return profileJpaRepository.existsByEmail(email);
    }
}
