package com.example.servigohex.application.ports.out;

import com.example.servigohex.domain.models.Profile;

import java.util.List;

public interface TokenProviderPort {

    String generateToken(Profile profile);

    String extractSubject(String token);

    List<String> extractRoles(String token);

    boolean isValid(String token, String email);
}
