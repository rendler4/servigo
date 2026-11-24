package com.example.servigohex.infrastructure.adapters.out.persistence;

import com.example.servigohex.domain.enums.IdentityStatus;
import com.example.servigohex.domain.enums.UserType;
import com.example.servigohex.domain.models.Profile;
import com.example.servigohex.infrastructure.adapters.out.persistence.entities.ProfileEntity;
import com.example.servigohex.infrastructure.adapters.out.persistence.repositories.ProfileJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfilePersistenceAdapterTest {

    @Mock
    private ProfileJpaRepository profileJpaRepository;

    @InjectMocks
    private ProfilePersistenceAdapter adapter;

    private ProfileEntity entity;

    @BeforeEach
    void setUp() {
        entity = new ProfileEntity();
        entity.setId(1L);
        entity.setEmail("ana@servigo.co");
        entity.setPasswordHash("hash");
        entity.setFullName("Ana Gómez");
        entity.setUserType(UserType.CLIENT);
        entity.setIdentityStatus(IdentityStatus.NOT_SUBMITTED);
    }

    @Test
    void findByEmail_existente_mapeaAlModeloDeDominio() {
        when(profileJpaRepository.findByEmail("ana@servigo.co")).thenReturn(Optional.of(entity));

        Optional<Profile> result = adapter.findByEmail("ana@servigo.co");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getUserType()).isEqualTo(UserType.CLIENT);
    }

    @Test
    void findByEmail_noExistente_devuelveVacio() {
        when(profileJpaRepository.findByEmail("nadie@servigo.co")).thenReturn(Optional.empty());

        assertThat(adapter.findByEmail("nadie@servigo.co")).isEmpty();
    }

    @Test
    void findById_existente_devuelvePerfil() {
        when(profileJpaRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertThat(adapter.findById(1L)).map(Profile::getEmail).contains("ana@servigo.co");
    }

    @Test
    void save_convierteAEntidadYDevuelveLoGuardado() {
        Profile nuevo = Profile.builder()
                .email("ana@servigo.co")
                .passwordHash("hash")
                .fullName("Ana Gómez")
                .userType(UserType.CLIENT)
                .build();
        when(profileJpaRepository.save(any(ProfileEntity.class))).thenReturn(entity);

        Profile result = adapter.save(nuevo);

        ArgumentCaptor<ProfileEntity> captor = ArgumentCaptor.forClass(ProfileEntity.class);
        verify(profileJpaRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("ana@servigo.co");
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void existsByEmail_delegaEnRepositorio() {
        when(profileJpaRepository.existsByEmail("ana@servigo.co")).thenReturn(true);

        assertThat(adapter.existsByEmail("ana@servigo.co")).isTrue();
    }
}
