package com.example.servigohex.infrastructure.adapters.out.security;

import com.example.servigohex.domain.enums.UserType;
import com.example.servigohex.domain.models.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityAdaptersTest {

    private JwtTokenAdapter jwtTokenAdapter;

    private final Profile proveedor = Profile.builder()
            .email("luis@servigo.co")
            .userType(UserType.PROVIDER)
            .build();

    @BeforeEach
    void setUp() {
        jwtTokenAdapter = new JwtTokenAdapter();
        ReflectionTestUtils.setField(jwtTokenAdapter, "secret",
                "secreto-de-pruebas-con-al-menos-256-bits-de-longitud-para-hmac");
        ReflectionTestUtils.setField(jwtTokenAdapter, "expirationMs", 3_600_000L);
    }

    @Test
    void generateToken_incluyeCorreoComoSubjectYRolDelPerfil() {
        String token = jwtTokenAdapter.generateToken(proveedor);

        assertThat(jwtTokenAdapter.extractSubject(token)).isEqualTo("luis@servigo.co");
        assertThat(jwtTokenAdapter.extractRoles(token)).containsExactly("PROVIDER");
    }

    @Test
    void isValid_mismoCorreoYVigente_esTrue() {
        String token = jwtTokenAdapter.generateToken(proveedor);

        assertThat(jwtTokenAdapter.isValid(token, "luis@servigo.co")).isTrue();
    }

    @Test
    void isValid_otroCorreo_esFalse() {
        String token = jwtTokenAdapter.generateToken(proveedor);

        assertThat(jwtTokenAdapter.isValid(token, "ana@servigo.co")).isFalse();
    }

    @Test
    void bcrypt_codificaYVerificaPassword() {
        BCryptPasswordAdapter bcrypt = new BCryptPasswordAdapter();

        String hash = bcrypt.encode("secreto1");

        assertThat(hash).isNotEqualTo("secreto1");
        assertThat(bcrypt.matches("secreto1", hash)).isTrue();
        assertThat(bcrypt.matches("otro", hash)).isFalse();
    }
}
