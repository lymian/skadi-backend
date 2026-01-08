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
@Schema(description = "Almacén/bodega para gestión de inventario")
public class AlmacenDTO {

    @Schema(description = "ID del almacén (solo lectura en respuestas)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre del almacén", example = "Almacén Central", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @Schema(description = "Dirección física del almacén", example = "Av. Industrial 123, Lima")
    private String direccion;

    @Schema(description = "Estado del almacén", example = "activo", allowableValues = { "activo",
            "inactivo" }, defaultValue = "activo")
    private String estado;
}
