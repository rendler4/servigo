package com.example.servigohex.application.ports.in;

import com.example.servigohex.domain.enums.UserType;
import com.example.servigohex.domain.models.Profile;

public interface AuthUseCase {

    // retorna el access token (JWT)
    String login(String email, String password);

    Profile register(String email, String password, String fullName, String phone, UserType userType);
}
