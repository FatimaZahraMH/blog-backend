package com.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Le nom d'utilisateur ou email est obligatoire")
    private String usernameOrEmail;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;
}
