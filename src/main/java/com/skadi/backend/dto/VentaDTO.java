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
@Schema(description = "Documento de venta (salida de inventario)")
public class VentaDTO {

    @Schema(description = "ID de la venta", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre del cliente", example = "Cliente XYZ E.I.R.L.")
    private String cliente;

    @Schema(description = "Fecha de la venta (si no se envía, usa fecha actual)", example = "2026-01-15T14:00:00", type = "string", format = "date-time")
    private LocalDateTime fecha;

    @Schema(description = "Número de documento (boleta, factura, etc.)", example = "BOL-001-0005678")
    private String numeroDocumento;

    @Schema(description = "ID del almacén de donde sale la mercadería", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El almacén es requerido")
    private Long almacenId;

    @Schema(description = "Total de la venta (calculado automáticamente)", example = "175.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal total;

    @Schema(description = "Lista de productos a vender (usa FIFO si no se especifica lote)", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    @NotNull(message = "Los detalles son requeridos")
    private List<VentaDetalleDTO> detalles;
}
