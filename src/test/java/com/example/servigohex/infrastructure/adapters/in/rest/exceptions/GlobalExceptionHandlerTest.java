package com.example.servigohex.infrastructure.adapters.in.rest.exceptions;

import com.example.servigohex.infrastructure.adapters.in.rest.dto.ApiError;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/prueba");

    @Test
    void excepcionNoControlada_devuelve500SinExponerDetalle() {
        ResponseEntity<ApiError> response = handler.handleGeneric(new IllegalStateException("detalle interno"), request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.getBody().getMessage()).doesNotContain("detalle interno");
        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/prueba");
    }

    @Test
    void excepcionDeAutenticacionDeSpring_devuelve401() {
        ResponseEntity<ApiError> response = handler.handleAuthenticationException(
                new BadCredentialsException("bad"), request);

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(response.getBody().getError()).isEqualTo("AUTHENTICATION_ERROR");
    }

    @Test
    void accesoDenegado_devuelve403() {
        ResponseEntity<ApiError> response = handler.handleAccessDenied(new AccessDeniedException("no"), request);

        assertThat(response.getStatusCode().value()).isEqualTo(403);
        assertThat(response.getBody().getError()).isEqualTo("ACCESS_DENIED");
    }
}
