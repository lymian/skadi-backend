package com.skadi.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos de la empresa (Tenant)")
public class EmpresaDTO {

    @Schema(description = "ID de la empresa", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre comercial", example = "Mi Empresa S.A.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @Schema(description = "RUC / Identificación Fiscal", example = "20123456789")
    private String ruc;

    @Schema(description = "Dirección fiscal", example = "Av. Principal 123, Oficina 401")
    private String direccion;

    @Schema(description = "Teléfono de contacto", example = "+51 987654321")
    private String telefono;

    @Schema(description = "Correo electrónico de contacto", example = "contacto@miempresa.com")
    private String email;

    @Schema(description = "Fecha de registro", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
}
