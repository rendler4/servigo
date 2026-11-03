package com.example.servigohex.infrastructure.adapters.in.rest.dto;

import com.example.servigohex.domain.enums.IdentityStatus;
import com.example.servigohex.domain.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Nunca incluye passwordHash: MapStruct solo copia los campos que existen aquí.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {

    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String bio;
    private String avatarUrl;
    private UserType userType;
    private IdentityStatus identityStatus;
    private LocalDateTime createdAt;
}
