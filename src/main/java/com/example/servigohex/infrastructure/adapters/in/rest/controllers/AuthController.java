package com.example.servigohex.infrastructure.adapters.in.rest.controllers;

import com.example.servigohex.application.ports.in.AuthUseCase;
import com.example.servigohex.domain.models.Profile;
import com.example.servigohex.infrastructure.adapters.in.rest.dto.LoginRequest;
import com.example.servigohex.infrastructure.adapters.in.rest.dto.ProfileResponse;
import com.example.servigohex.infrastructure.adapters.in.rest.dto.RegisterRequest;
import com.example.servigohex.infrastructure.adapters.in.rest.dto.TokenResponse;
import com.example.servigohex.infrastructure.adapters.in.rest.mappers.ProfileMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Registro y login de perfiles (clientes y proveedores)")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener un JWT")
    @ApiResponse(responseCode = "200", description = "Login exitoso, retorna el token")
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authUseCase.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new TokenResponse(token, "Bearer"));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo perfil (cliente o proveedor)")
    @ApiResponse(responseCode = "201", description = "Perfil creado exitosamente")
    @ApiResponse(responseCode = "409", description = "Ya existe un perfil con ese correo")
    public ResponseEntity<ProfileResponse> register(@Valid @RequestBody RegisterRequest request) {
        Profile profile = authUseCase.register(
                request.getEmail(),
                request.getPassword(),
                request.getFullName(),
                request.getPhone(),
                request.getUserType());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ProfileMapper.INSTANCE.toProfileResponse(profile));
    }
}
