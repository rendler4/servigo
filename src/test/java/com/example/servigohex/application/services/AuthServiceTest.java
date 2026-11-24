package com.example.servigohex.application.services;

import com.example.servigohex.application.ports.out.PasswordEncoderPort;
import com.example.servigohex.application.ports.out.ProfileRepositoryPort;
import com.example.servigohex.application.ports.out.TokenProviderPort;
import com.example.servigohex.domain.enums.IdentityStatus;
import com.example.servigohex.domain.enums.UserType;
import com.example.servigohex.domain.exceptions.DuplicateEmailException;
import com.example.servigohex.domain.exceptions.InvalidCredentialsException;
import com.example.servigohex.domain.models.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private ProfileRepositoryPort profileRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private TokenProviderPort tokenProviderPort;

    @InjectMocks
    private AuthService authService;

    private Profile profile;

    @BeforeEach
    void setUp() {
        profile = Profile.builder()
                .id(1L)
                .email("ana@servigo.co")
                .passwordHash("hash")
                .fullName("Ana Gómez")
                .userType(UserType.CLIENT)
                .identityStatus(IdentityStatus.NOT_SUBMITTED)
                .build();
    }

    @Test
    void login_credencialesValidas_retornaToken() {
        when(profileRepositoryPort.findByEmail("ana@servigo.co")).thenReturn(Optional.of(profile));
        when(passwordEncoderPort.matches("secreto1", "hash")).thenReturn(true);
        when(tokenProviderPort.generateToken(profile)).thenReturn("jwt-generado");

        String token = authService.login("ana@servigo.co", "secreto1");

        assertThat(token).isEqualTo("jwt-generado");
    }

    @Test
    void login_correoNoRegistrado_lanzaInvalidCredentials() {
        when(profileRepositoryPort.findByEmail("nadie@servigo.co")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login("nadie@servigo.co", "secreto1"))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(tokenProviderPort, never()).generateToken(any());
    }

    @Test
    void login_passwordIncorrecto_lanzaInvalidCredentials() {
        when(profileRepositoryPort.findByEmail("ana@servigo.co")).thenReturn(Optional.of(profile));
        when(passwordEncoderPort.matches("malo", "hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login("ana@servigo.co", "malo"))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(tokenProviderPort, never()).generateToken(any());
    }

    @Test
    void register_correoNuevo_guardaPerfilConPasswordCifradoYIdentidadSinEnviar() {
        when(profileRepositoryPort.existsByEmail("luis@servigo.co")).thenReturn(false);
        when(passwordEncoderPort.encode("secreto1")).thenReturn("hash-bcrypt");
        when(profileRepositoryPort.save(any(Profile.class))).thenAnswer(inv -> inv.getArgument(0));

        Profile result = authService.register("luis@servigo.co", "secreto1", "Luis Pérez",
                "3004445566", UserType.PROVIDER);

        ArgumentCaptor<Profile> captor = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepositoryPort).save(captor.capture());
        Profile guardado = captor.getValue();
        assertThat(guardado.getEmail()).isEqualTo("luis@servigo.co");
        assertThat(guardado.getPasswordHash()).isEqualTo("hash-bcrypt");
        assertThat(guardado.getUserType()).isEqualTo(UserType.PROVIDER);
        assertThat(guardado.getIdentityStatus()).isEqualTo(IdentityStatus.NOT_SUBMITTED);
        assertThat(result).isSameAs(guardado);
    }

    @Test
    void register_correoDuplicado_lanzaDuplicateEmailYNoGuarda() {
        when(profileRepositoryPort.existsByEmail("ana@servigo.co")).thenReturn(true);

        assertThatThrownBy(() -> authService.register("ana@servigo.co", "secreto1", "Ana",
                null, UserType.CLIENT))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("ana@servigo.co");

        verify(profileRepositoryPort, never()).save(any());
        verify(passwordEncoderPort, never()).encode(anyString());
    }
}
