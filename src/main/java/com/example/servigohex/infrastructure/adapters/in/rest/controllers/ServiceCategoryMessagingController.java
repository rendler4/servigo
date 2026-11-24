package com.example.servigohex.infrastructure.adapters.in.rest.controllers;

import com.example.servigohex.application.ports.in.ServiceCategoryMessagingUseCase;
import com.example.servigohex.infrastructure.adapters.in.rest.dto.ServiceCategoryRequest;
import com.example.servigohex.infrastructure.adapters.in.rest.mappers.ServiceCategoryMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Canal alterno (cola RabbitMQ) para crear categorías de forma asíncrona,
// sin bloquear la respuesta HTTP al cliente.
@RestController
@RequestMapping("/api/v1/categorias/cola")
@Tag(name = "Categorías - Mensajería", description = "Creación asíncrona de categorías vía RabbitMQ")
public class ServiceCategoryMessagingController {

    private final ServiceCategoryMessagingUseCase serviceCategoryMessagingUseCase;

    public ServiceCategoryMessagingController(ServiceCategoryMessagingUseCase serviceCategoryMessagingUseCase) {
        this.serviceCategoryMessagingUseCase = serviceCategoryMessagingUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Envía la creación de una categoría a la cola de mensajería",
            description = "Publica el mensaje en RabbitMQ; el consumer crea la categoría en base de datos")
    @ApiResponse(responseCode = "202", description = "Mensaje encolado exitosamente")
    @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene rol PROVIDER")
    public ResponseEntity<Void> enviarCreacion(@Valid @RequestBody ServiceCategoryRequest request) {
        serviceCategoryMessagingUseCase.enviarCreacion(ServiceCategoryMapper.INSTANCE.toServiceCategory(request));
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
