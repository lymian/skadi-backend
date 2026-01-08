package com.skadi.backend.controllers;

import com.skadi.backend.dto.AuthResponse;
import com.skadi.backend.dto.LoginRequest;
import com.skadi.backend.dto.RegisterRequest;
import com.skadi.backend.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de registro, login y sesión de usuario")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Registrar nueva empresa con usuario admin", description = "Crea una nueva empresa y su primer usuario administrador. Retorna token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro exitoso", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o username ya existe")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica usuario y retorna token JWT para usar en endpoints protegidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Obtener usuario actual", description = "Retorna información del usuario autenticado basado en el token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información del usuario"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(Authentication authentication) {
        return ResponseEntity.ok(authService.me(authentication.getName()));
    }
}
