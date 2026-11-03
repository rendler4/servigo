package com.example.servigohex.infrastructure.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.example.servigohex.infrastructure.adapters.out.persistence")
@EntityScan("com.example.servigohex.infrastructure.adapters.out.persistence.entities")
@ComponentScan("com.example.servigohex")
public class ServigoHexApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServigoHexApplication.class, args);
    }
}
