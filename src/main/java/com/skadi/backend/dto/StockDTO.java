package com.skadi.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Stock actual de una variante en un almacén")
public class StockDTO {

    @Schema(description = "ID del almacén", example = "1")
    private Long almacenId;

    @Schema(description = "Nombre del almacén", example = "Almacén Central")
    private String almacenNombre;

    @Schema(description = "ID de la variante", example = "1")
    private Long varianteId;

    @Schema(description = "Nombre de la variante", example = "Negro - 15.6\"")
    private String varianteNombre;

    @Schema(description = "Nombre del producto", example = "Laptop HP Pavilion")
    private String productoNombre;

    @Schema(description = "SKU de la variante", example = "HP-PAV-15-NGR")
    private String sku;

    @Schema(description = "ID del lote (si aplica)", example = "1")
    private Long loteId;

    @Schema(description = "Código del lote", example = "LOTE-2026-001")
    private String codigoLote;

    @Schema(description = "Cantidad en stock", example = "95")
    private Integer stock;

    @Schema(description = "Costo promedio ponderado unitario", example = "25.50")
    private BigDecimal costoPromedio;

    @Schema(description = "Valor total del stock (stock × costo promedio)", example = "2422.50")
    private BigDecimal valorTotal;
}
