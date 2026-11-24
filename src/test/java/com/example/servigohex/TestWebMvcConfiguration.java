package com.example.servigohex;

import org.springframework.boot.SpringBootConfiguration;

/**
 * Configuración mínima usada únicamente por los tests {@code @WebMvcTest}.
 * <p>
 * Está en el paquete raíz ({@code com.example.servigohex}) porque Spring Boot
 * busca una {@code @SpringBootConfiguration} en los paquetes ancestros del test,
 * y la clase principal vive en {@code infrastructure.config}, fuera de esa ruta.
 * Deliberadamente NO declara {@code @ComponentScan}: cada test importa (vía
 * {@code @Import}) solo el controlador bajo prueba y la seguridad, para no
 * levantar adaptadores que requieren PostgreSQL, Redis o RabbitMQ.
 */
@SpringBootConfiguration
public class TestWebMvcConfiguration {
}
