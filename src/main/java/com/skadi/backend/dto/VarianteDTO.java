package com.skadi.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Variante de producto (talla, color, presentación)")
public class VarianteDTO {

    @Schema(description = "ID de la variante", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID del producto padre", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long productoId;

    @Schema(description = "Nombre de la variante", example = "Negro - 15.6\"", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @Schema(description = "Código SKU único", example = "HP-PAV-15-NGR")
    private String sku;

    @Schema(description = "Precio de compra referencial", example = "2500.00")
    private BigDecimal precioCompra;

    @Schema(description = "Precio de venta", example = "3200.00")
    private BigDecimal precioVenta;
}
