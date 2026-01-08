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
@Schema(description = "Usuario de la empresa")
public class UsuarioDTO {

    @Schema(description = "ID del usuario", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre de usuario único", example = "operador1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El username es requerido")
    private String username;

    @Schema(description = "Contraseña (solo para creación/actualización, no se retorna en respuestas)", example = "password123", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Schema(description = "Rol del usuario", example = "operador", allowableValues = { "admin", "operador",
            "consulta" }, defaultValue = "consulta")
    private String rol;
}
