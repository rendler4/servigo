package com.example.servigohex.infrastructure.adapters.in.rest.controllers;

import com.example.servigohex.application.ports.in.ServiceCategoryUseCase;
import com.example.servigohex.domain.models.ServiceCategory;
import com.example.servigohex.infrastructure.adapters.in.rest.dto.ServiceCategoryRequest;
import com.example.servigohex.infrastructure.adapters.in.rest.dto.ServiceCategoryResponse;
import com.example.servigohex.infrastructure.adapters.in.rest.mappers.ServiceCategoryMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@Tag(name = "Categorías de servicio", description = "Catálogo de categorías (lectura pública, cacheada en Redis)")
public class ServiceCategoryController {

    private final ServiceCategoryUseCase serviceCategoryUseCase;

    public ServiceCategoryController(ServiceCategoryUseCase serviceCategoryUseCase) {
        this.serviceCategoryUseCase = serviceCategoryUseCase;
    }

    @GetMapping
    @Operation(summary = "Listar categorías activas", description = "Lista cacheada en Redis bajo la key 'categorias::activas'")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public ResponseEntity<List<ServiceCategoryResponse>> listar() {
        List<ServiceCategoryResponse> response = serviceCategoryUseCase.listarActivas().stream()
                .map(ServiceCategoryMapper.INSTANCE::toServiceCategoryResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoría por id")
    @ApiResponse(responseCode = "200", description = "Categoría encontrada")
    @ApiResponse(responseCode = "404", description = "No existe una categoría con ese id")
    public ResponseEntity<ServiceCategoryResponse> buscar(@PathVariable Long id) {
        ServiceCategory categoria = serviceCategoryUseCase.buscarPorId(id);
        return ResponseEntity.ok(ServiceCategoryMapper.INSTANCE.toServiceCategoryResponse(categoria));
    }

    @PostMapping
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Crear categoría", description = "Solo proveedores. Invalida el cache de categorías activas")
    @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente")
    @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene rol PROVIDER")
    @ApiResponse(responseCode = "409", description = "Ya existe una categoría con ese nombre")
    public ResponseEntity<ServiceCategoryResponse> crear(@Valid @RequestBody ServiceCategoryRequest request) {
        ServiceCategory categoria = serviceCategoryUseCase.crear(
                ServiceCategoryMapper.INSTANCE.toServiceCategory(request));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ServiceCategoryMapper.INSTANCE.toServiceCategoryResponse(categoria));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Actualizar categoría", description = "Solo proveedores. Invalida el cache de categorías activas")
    @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente")
    @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene rol PROVIDER")
    @ApiResponse(responseCode = "404", description = "No existe una categoría con ese id")
    public ResponseEntity<ServiceCategoryResponse> actualizar(@PathVariable Long id,
                                                               @Valid @RequestBody ServiceCategoryRequest request) {
        ServiceCategory categoria = serviceCategoryUseCase.actualizar(
                id, ServiceCategoryMapper.INSTANCE.toServiceCategory(request));
        return ResponseEntity.ok(ServiceCategoryMapper.INSTANCE.toServiceCategoryResponse(categoria));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Desactivar categoría", description = "Borrado lógico (active=false). Solo proveedores, invalida el cache")
    @ApiResponse(responseCode = "204", description = "Categoría desactivada exitosamente")
    @ApiResponse(responseCode = "403", description = "El usuario autenticado no tiene rol PROVIDER")
    @ApiResponse(responseCode = "404", description = "No existe una categoría con ese id")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        serviceCategoryUseCase.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
