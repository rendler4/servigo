package com.example.servigohex.infrastructure.adapters.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceCategoryRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String description;
}
