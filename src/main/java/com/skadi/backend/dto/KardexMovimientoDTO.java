package com.skadi.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Movimiento de kardex valorizado")
public class KardexMovimientoDTO {

    @Schema(description = "ID del movimiento", example = "1")
    private Long id;

    @Schema(description = "ID del almacén", example = "1")
    private Long almacenId;

    @Schema(description = "Nombre del almacén", example = "Almacén Central")
    private String almacenNombre;

    @Schema(description = "ID de la variante", example = "1")
    private Long varianteId;

    @Schema(description = "Nombre de la variante", example = "Negro - 15.6\"")
    private String varianteNombre;

    @Schema(description = "ID del lote (si aplica)", example = "1")
    private Long loteId;

    @Schema(description = "Código del lote", example = "LOTE-2026-001")
    private String codigoLote;

    @Schema(description = "Fecha y hora del movimiento", example = "2026-01-15T10:30:00")
    private LocalDateTime fecha;

    @Schema(description = "Tipo de movimiento", example = "entrada", allowableValues = { "entrada", "salida",
            "ajuste" })
    private String tipo;

    @Schema(description = "Cantidad del movimiento", example = "100")
    private Integer cantidad;

    @Schema(description = "Costo unitario del movimiento", example = "25.50")
    private BigDecimal costoUnitario;

    @Schema(description = "Costo total del movimiento", example = "2550.00")
    private BigDecimal costoTotal;

    @Schema(description = "Saldo en cantidad después del movimiento", example = "100")
    private Integer saldoCantidad;

    @Schema(description = "Costo unitario promedio del saldo", example = "25.50")
    private BigDecimal saldoCostoUnitario;

    @Schema(description = "Costo total del saldo", example = "2550.00")
    private BigDecimal saldoCostoTotal;

    @Schema(description = "Referencia del documento origen", example = "compra")
    private String referencia;
}
