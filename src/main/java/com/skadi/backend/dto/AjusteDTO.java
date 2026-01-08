package com.skadi.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ajuste de inventario (correcciones, pérdidas, inventario físico)")
public class AjusteDTO {

    @Schema(description = "ID del ajuste", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Fecha del ajuste", example = "2026-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fecha;

    @Schema(description = "ID del almacén donde se realiza el ajuste", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El almacén es requerido")
    private Long almacenId;

    @Schema(description = "Nombre del almacén", example = "Almacén Central", accessMode = Schema.AccessMode.READ_ONLY)
    private String almacenNombre;

    @Schema(description = "ID de la variante a ajustar", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La variante es requerida")
    private Long varianteId;

    @Schema(description = "Nombre de la variante", example = "Ibuprofeno 400mg x 100", accessMode = Schema.AccessMode.READ_ONLY)
    private String varianteNombre;

    @Schema(description = "ID del lote a ajustar (opcional)", example = "1")
    private Long loteId;

    @Schema(description = "Código del lote", example = "LOTE-2026-001", accessMode = Schema.AccessMode.READ_ONLY)
    private String codigoLote;

    @Schema(description = "Stock antes del ajuste", example = "100", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer stockAntes;

    @Schema(description = "Nuevo valor de stock después del ajuste", example = "95", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El nuevo stock es requerido")
    private Integer nuevoStock;

    @Schema(description = "Motivo del ajuste", example = "Inventario físico - diferencia encontrada", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El motivo es requerido")
    private String motivo;
}
