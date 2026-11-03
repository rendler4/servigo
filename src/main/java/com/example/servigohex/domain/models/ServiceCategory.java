package com.example.servigohex.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceCategory {

    private Long id;
    private String name;
    private String description;
    private boolean active;
}
