package com.skadi.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta de autenticación con token JWT")
public class AuthResponse {

    @Schema(description = "Token JWT para usar en header Authorization", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "ID de la empresa del usuario", example = "1")
    private Long empresaId;

    @Schema(description = "Nombre de la empresa", example = "Distribuidora ABC S.A.C.")
    private String empresaNombre;

    @Schema(description = "Nombre de usuario autenticado", example = "admin")
    private String username;

    @Schema(description = "Rol del usuario", example = "admin", allowableValues = { "admin", "operador", "consulta" })
    private String rol;
}
