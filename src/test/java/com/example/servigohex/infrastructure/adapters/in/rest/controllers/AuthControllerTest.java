package com.example.servigohex.infrastructure.adapters.in.rest.controllers;

import com.example.servigohex.TestWebMvcConfiguration;
import com.example.servigohex.application.ports.in.AuthUseCase;
import com.example.servigohex.application.ports.out.TokenProviderPort;
import com.example.servigohex.domain.enums.IdentityStatus;
import com.example.servigohex.domain.enums.UserType;
import com.example.servigohex.domain.exceptions.DuplicateEmailException;
import com.example.servigohex.domain.exceptions.InvalidCredentialsException;
import com.example.servigohex.domain.models.Profile;
import com.example.servigohex.infrastructure.adapters.in.rest.exceptions.GlobalExceptionHandler;
import com.example.servigohex.infrastructure.adapters.in.rest.security.JwtAuthenticationFilter;
import com.example.servigohex.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class,
        AuthController.class, TestWebMvcConfiguration.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthUseCase authUseCase;

    @MockBean
    private TokenProviderPort tokenProviderPort;

    private static final String LOGIN_BODY = """
            { "email": "ana@servigo.co", "password": "secreto1" }
            """;

    private static final String REGISTER_BODY = """
            {
              "email": "ana@servigo.co",
              "password": "secreto1",
              "fullName": "Ana Gómez",
              "phone": "3001112233",
              "userType": "CLIENT"
            }
            """;

    @Test
    void login_credencialesValidas_devuelve200ConToken() throws Exception {
        when(authUseCase.login("ana@servigo.co", "secreto1")).thenReturn("jwt-generado");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-generado"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void login_credencialesInvalidas_devuelve401() throws Exception {
        when(authUseCase.login(anyString(), anyString()))
                .thenThrow(new InvalidCredentialsException("Credenciales inválidas"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_BODY))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("AUTHENTICATION_ERROR"));
    }

    @Test
    void login_sinPassword_devuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"ana@servigo.co\" }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void register_datosValidos_devuelve201SinPasswordHash() throws Exception {
        Profile creado = Profile.builder()
                .id(1L)
                .email("ana@servigo.co")
                .passwordHash("hash-que-no-debe-salir")
                .fullName("Ana Gómez")
                .userType(UserType.CLIENT)
                .identityStatus(IdentityStatus.NOT_SUBMITTED)
                .build();
        when(authUseCase.register(eq("ana@servigo.co"), eq("secreto1"), eq("Ana Gómez"),
                eq("3001112233"), eq(UserType.CLIENT))).thenReturn(creado);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userType").value("CLIENT"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void register_correoDuplicado_devuelve409() throws Exception {
        when(authUseCase.register(anyString(), anyString(), anyString(), anyString(), any()))
                .thenThrow(new DuplicateEmailException("Ya existe un perfil con el correo ana@servigo.co"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_BODY))
                .andExpect(status().isConflict());
    }
}
