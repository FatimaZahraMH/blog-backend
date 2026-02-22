package com.blog.controller;

import com.blog.constants.ApiRoutes;
import com.blog.dto.request.LoginRequest;
import com.blog.dto.request.RegisterRequest;
import com.blog.dto.response.AuthResponse;
import com.blog.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiRoutes.AUTH )
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Endpoints pour l'inscription et la connexion")
public class AuthController {

    private final AuthService authService;

    @PostMapping(ApiRoutes.AUTH_REGISTER)
    @Operation(summary = "Créer un nouveau compte utilisateur")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping(ApiRoutes.AUTH_LOGIN)
    @Operation(summary = "Se connecter et obtenir un token JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
