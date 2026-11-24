package com.example.servigohex.infrastructure.adapters.in.rest.controllers;

import com.example.servigohex.TestWebMvcConfiguration;
import com.example.servigohex.application.ports.in.ServiceCategoryMessagingUseCase;
import com.example.servigohex.application.ports.out.TokenProviderPort;
import com.example.servigohex.domain.models.ServiceCategory;
import com.example.servigohex.infrastructure.adapters.in.rest.exceptions.GlobalExceptionHandler;
import com.example.servigohex.infrastructure.adapters.in.rest.security.JwtAuthenticationFilter;
import com.example.servigohex.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ServiceCategoryMessagingController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class,
        ServiceCategoryMessagingController.class, TestWebMvcConfiguration.class})
class ServiceCategoryMessagingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceCategoryMessagingUseCase serviceCategoryMessagingUseCase;

    @MockBean
    private TokenProviderPort tokenProviderPort;

    @Test
    void enviarCreacion_conRolProvider_encolaYDevuelve202() throws Exception {
        mockMvc.perform(post("/api/v1/categorias/cola")
                        .with(user("luis@servigo.co").roles("PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"Pintura\", \"description\": \"Interiores\" }"))
                .andExpect(status().isAccepted());

        ArgumentCaptor<ServiceCategory> captor = ArgumentCaptor.forClass(ServiceCategory.class);
        verify(serviceCategoryMessagingUseCase).enviarCreacion(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Pintura");
    }

    @Test
    void enviarCreacion_conRolClient_devuelve403() throws Exception {
        mockMvc.perform(post("/api/v1/categorias/cola")
                        .with(user("ana@servigo.co").roles("CLIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"Pintura\" }"))
                .andExpect(status().isForbidden());

        verify(serviceCategoryMessagingUseCase, never()).enviarCreacion(any());
    }

    @Test
    void enviarCreacion_sinNombre_devuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/categorias/cola")
                        .with(user("luis@servigo.co").roles("PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ }"))
                .andExpect(status().isBadRequest());
    }
}
