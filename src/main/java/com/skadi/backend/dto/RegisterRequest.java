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
@Schema(description = "Request para registrar nueva empresa con usuario administrador")
public class RegisterRequest {

    @Schema(description = "Nombre de la empresa a crear", example = "Distribuidora ABC S.A.C.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre de empresa es requerido")
    private String empresaNombre;

    @Schema(description = "Nombre de usuario para el administrador", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El username es requerido")
    private String username;

    @Schema(description = "Contraseña del usuario", example = "admin123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "La contraseña es requerida")
    private String password;

    @Schema(description = "Rol del usuario (admin, operador, consulta). Por defecto: admin", example = "admin", defaultValue = "admin")
    private String rol;
}
