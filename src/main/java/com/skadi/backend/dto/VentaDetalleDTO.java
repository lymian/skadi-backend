package com.skadi.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Detalle de línea de venta")
public class VentaDetalleDTO {

    @Schema(description = "ID del detalle", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID de la variante a vender", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La variante es requerida")
    private Long varianteId;

    @Schema(description = "ID de lote específico (opcional, si no se especifica usa FIFO automático)", example = "1")
    private Long loteId;

    @Schema(description = "Cantidad a vender", example = "5", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La cantidad es requerida")
    @Positive(message = "La cantidad debe ser positiva")
    private Integer cantidad;

    @Schema(description = "Precio unitario de venta", example = "35.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El precio unitario es requerido")
    @Positive(message = "El precio unitario debe ser positivo")
    private BigDecimal precioUnitario;
}
