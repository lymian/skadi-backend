package com.skadi.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Detalle de línea de compra")
public class CompraDetalleDTO {

    @Schema(description = "ID del detalle", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID de la variante a comprar", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La variante es requerida")
    private Long varianteId;

    @Schema(description = "ID de lote existente (opcional, usar si el lote ya existe)", example = "1")
    private Long loteId;

    @Schema(description = "Código para crear nuevo lote (opcional, se crea automáticamente si se especifica)", example = "LOTE-2026-001")
    private String codigoLoteNuevo;

    @Schema(description = "Fecha de vencimiento del nuevo lote (requerido si el producto requiereLote=true)", example = "2027-12-31", type = "string", format = "date")
    private LocalDate fechaVencimientoLote;

    @Schema(description = "Cantidad a ingresar", example = "100", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La cantidad es requerida")
    @Positive(message = "La cantidad debe ser positiva")
    private Integer cantidad;

    @Schema(description = "Costo unitario de compra", example = "25.50", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El costo unitario es requerido")
    @Positive(message = "El costo unitario debe ser positivo")
    private BigDecimal costoUnitario;
}
