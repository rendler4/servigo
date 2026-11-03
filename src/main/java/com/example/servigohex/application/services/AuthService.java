package com.example.servigohex.application.services;

import com.example.servigohex.application.ports.in.AuthUseCase;
import com.example.servigohex.application.ports.out.PasswordEncoderPort;
import com.example.servigohex.application.ports.out.ProfileRepositoryPort;
import com.example.servigohex.application.ports.out.TokenProviderPort;
import com.example.servigohex.domain.enums.IdentityStatus;
import com.example.servigohex.domain.enums.UserType;
import com.example.servigohex.domain.exceptions.DuplicateEmailException;
import com.example.servigohex.domain.exceptions.InvalidCredentialsException;
import com.example.servigohex.domain.models.Profile;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements AuthUseCase {

    private final ProfileRepositoryPort profileRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenProviderPort tokenProviderPort;

    public AuthService(ProfileRepositoryPort profileRepositoryPort,
                       PasswordEncoderPort passwordEncoderPort,
                       TokenProviderPort tokenProviderPort) {
        this.profileRepositoryPort = profileRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenProviderPort = tokenProviderPort;
    }

    @Override
    public String login(String email, String password) {
        Profile profile = profileRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales inválidas"));

        if (!passwordEncoderPort.matches(password, profile.getPasswordHash())) {
            throw new InvalidCredentialsException("Credenciales inválidas");
        }

        return tokenProviderPort.generateToken(profile);
    }

    @Override
    public Profile register(String email, String password, String fullName, String phone, UserType userType) {
        if (profileRepositoryPort.existsByEmail(email)) {
            throw new DuplicateEmailException("Ya existe un perfil con el correo " + email);
        }

        Profile profile = Profile.builder()
                .email(email)
                .passwordHash(passwordEncoderPort.encode(password))
                .fullName(fullName)
                .phone(phone)
                .userType(userType)
                .identityStatus(IdentityStatus.NOT_SUBMITTED)
                .build();

        return profileRepositoryPort.save(profile);
    }
}
