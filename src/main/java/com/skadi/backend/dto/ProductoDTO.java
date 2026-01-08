package com.skadi.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Producto base con sus variantes")
public class ProductoDTO {

    @Schema(description = "ID del producto", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Código único del producto", example = "PROD-001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El código es requerido")
    private String codigo;

    @Schema(description = "Nombre del producto", example = "Laptop HP Pavilion", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @Schema(description = "Categoría del producto", example = "Electrónicos")
    private String categoria;

    @Schema(description = "Descripción detallada del producto", example = "Laptop HP Pavilion 15.6\" Core i5 8GB RAM")
    private String descripcion;

    @Schema(description = "Unidad de medida base", example = "unidad")
    private String unidadBase;

    @Schema(description = "Estado del producto", example = "activo", allowableValues = { "activo", "inactivo" })
    private String estado;

    @Schema(description = "Si true, las compras/ventas de este producto requieren especificar lote", example = "true", defaultValue = "false")
    private Boolean requiereLote;

    @Schema(description = "Lista de variantes del producto (solo en respuestas)", accessMode = Schema.AccessMode.READ_ONLY)
    private List<VarianteDTO> variantes;
}
