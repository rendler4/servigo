package com.example.servigohex.infrastructure.adapters.out.security;

import com.example.servigohex.application.ports.out.TokenProviderPort;
import com.example.servigohex.domain.models.Profile;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
public class JwtTokenAdapter implements TokenProviderPort {

    @Value("${app.security.jwt.secret}")
    private String secret;

    @Value("${app.security.jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public String generateToken(Profile profile) {
        return Jwts.builder()
                .subject(profile.getEmail())
                .claim("rol", List.of(profile.getUserType().name()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String extractSubject(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = extraerTodosLosClaims(token);
        return claims.get("rol", List.class);
    }

    @Override
    public boolean isValid(String token, String email) {
        String subjectExtraido = extractSubject(token);
        return subjectExtraido.equals(email) && !esTokenExpirado(token);
    }

    private boolean esTokenExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extraerClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extraerTodosLosClaims(token);
        return resolver.apply(claims);
    }

    private Claims extraerTodosLosClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
