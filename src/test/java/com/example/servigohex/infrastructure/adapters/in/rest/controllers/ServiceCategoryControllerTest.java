package com.example.servigohex.infrastructure.adapters.in.rest.controllers;

import com.example.servigohex.TestWebMvcConfiguration;
import com.example.servigohex.application.ports.in.ServiceCategoryUseCase;
import com.example.servigohex.application.ports.out.TokenProviderPort;
import com.example.servigohex.domain.exceptions.DuplicateCategoryNameException;
import com.example.servigohex.domain.exceptions.ResourceNotFoundException;
import com.example.servigohex.domain.models.ServiceCategory;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ServiceCategoryController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class,
        ServiceCategoryController.class, TestWebMvcConfiguration.class})
class ServiceCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceCategoryUseCase serviceCategoryUseCase;

    @MockBean
    private TokenProviderPort tokenProviderPort;

    private static final String BODY = """
            { "name": "Plomería", "description": "Reparación de fugas" }
            """;

    private ServiceCategory plomeria() {
        return ServiceCategory.builder().id(1L).name("Plomería").description("Reparación de fugas").active(true).build();
    }

    @Test
    void listar_esPublicoYDevuelve200() throws Exception {
        when(serviceCategoryUseCase.listarActivas()).thenReturn(List.of(plomeria()));

        mockMvc.perform(get("/api/v1/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Plomería"));
    }

    @Test
    void buscar_existente_devuelve200() throws Exception {
        when(serviceCategoryUseCase.buscarPorId(1L)).thenReturn(plomeria());

        mockMvc.perform(get("/api/v1/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void buscar_noExistente_devuelve404() throws Exception {
        when(serviceCategoryUseCase.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("No se encontró la categoría con id 99"));

        mockMvc.perform(get("/api/v1/categorias/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void crear_conRolProvider_devuelve201() throws Exception {
        when(serviceCategoryUseCase.crear(any(ServiceCategory.class))).thenReturn(plomeria());

        mockMvc.perform(post("/api/v1/categorias")
                        .with(user("luis@servigo.co").roles("PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Plomería"));
    }

    @Test
    void crear_conRolClient_devuelve403() throws Exception {
        mockMvc.perform(post("/api/v1/categorias")
                        .with(user("ana@servigo.co").roles("CLIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isForbidden());

        verify(serviceCategoryUseCase, never()).crear(any());
    }

    @Test
    void crear_sinAutenticar_devuelve403() throws Exception {
        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    void crear_sinNombre_devuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/categorias")
                        .with(user("luis@servigo.co").roles("PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"description\": \"sin nombre\" }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_nombreDuplicado_devuelve409() throws Exception {
        when(serviceCategoryUseCase.crear(any(ServiceCategory.class)))
                .thenThrow(new DuplicateCategoryNameException("Ya existe una categoría con el nombre Plomería"));

        mockMvc.perform(post("/api/v1/categorias")
                        .with(user("luis@servigo.co").roles("PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isConflict());
    }

    @Test
    void actualizar_conRolProvider_devuelve200() throws Exception {
        when(serviceCategoryUseCase.actualizar(eq(1L), any(ServiceCategory.class))).thenReturn(plomeria());

        mockMvc.perform(put("/api/v1/categorias/1")
                        .with(user("luis@servigo.co").roles("PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isOk());
    }

    @Test
    void desactivar_conRolProvider_devuelve204() throws Exception {
        mockMvc.perform(delete("/api/v1/categorias/1")
                        .with(user("luis@servigo.co").roles("PROVIDER")))
                .andExpect(status().isNoContent());

        verify(serviceCategoryUseCase).desactivar(1L);
    }
}
