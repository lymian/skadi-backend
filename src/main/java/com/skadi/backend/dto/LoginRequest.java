package com.skadi.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request para iniciar sesión")
public class LoginRequest {

    @Schema(description = "Nombre de usuario", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El username es requerido")
    private String username;

    @Schema(description = "Contraseña del usuario", example = "admin123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "La contraseña es requerida")
    private String password;
}
