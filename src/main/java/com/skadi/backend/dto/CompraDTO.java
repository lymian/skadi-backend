package com.skadi.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Documento de compra (entrada de inventario)")
public class CompraDTO {

    @Schema(description = "ID de la compra", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre del proveedor", example = "Proveedor ABC S.A.C.")
    private String proveedor;

    @Schema(description = "Fecha de la compra (si no se envía, usa fecha actual)", example = "2026-01-15T10:30:00", type = "string", format = "date-time")
    private LocalDateTime fecha;

    @Schema(description = "Número de documento (factura, guía, etc.)", example = "FAC-001-0001234")
    private String numeroDocumento;

    @Schema(description = "ID del almacén donde ingresa la mercadería", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El almacén es requerido")
    private Long almacenId;

    @Schema(description = "Total de la compra (calculado automáticamente)", example = "2550.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal total;

    @Schema(description = "Lista de productos a comprar", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    @NotNull(message = "Los detalles son requeridos")
    private List<CompraDetalleDTO> detalles;
}
