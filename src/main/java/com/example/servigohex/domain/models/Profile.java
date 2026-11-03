package com.example.servigohex.domain.models;

import com.example.servigohex.domain.enums.IdentityStatus;
import com.example.servigohex.domain.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Profile {

    private Long id;
    private String email;
    private String passwordHash;
    private String fullName;
    private String phone;
    private String bio;
    private String avatarUrl;
    private UserType userType;
    private String identityDocumentUrl;
    private IdentityStatus identityStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
