package com.skadi.backend.controllers;

import com.skadi.backend.dto.KardexMovimientoDTO;
import com.skadi.backend.dto.LoteDTO;
import com.skadi.backend.dto.StockDTO;
import com.skadi.backend.services.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Reportes de inventario y kardex valorizado")
public class ReporteController {

    private final ReporteService reporteService;

    @Operation(summary = "Stock general por almacén", description = "Obtiene el stock actual de todos los productos por almacén y lote")
    @ApiResponse(responseCode = "200", description = "Lista de stock")
    @GetMapping("/stock-general")
    public ResponseEntity<List<StockDTO>> getStockGeneral() {
        return ResponseEntity.ok(reporteService.getStockGeneral());
    }

    @Operation(summary = "Valorización del inventario", description = "Calcula el valor total del inventario usando costo promedio ponderado")
    @ApiResponse(responseCode = "200", description = "Valorización con detalle")
    @GetMapping("/valorizacion")
    public ResponseEntity<Map<String, Object>> getValorizacion() {
        return ResponseEntity.ok(reporteService.getValorizacion());
    }

    @Operation(summary = "Lotes próximos a vencer", description = "Lista lotes cuya fecha de vencimiento está dentro del rango especificado")
    @ApiResponse(responseCode = "200", description = "Lista de lotes por vencer")
    @GetMapping("/lotes-por-vencer")
    public ResponseEntity<List<LoteDTO>> getLotesPorVencer(
            @Parameter(description = "Número de días a futuro para considerar") @RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(reporteService.getLotesPorVencer(dias));
    }

    @Operation(summary = "Kardex de un producto", description = "Muestra todos los movimientos kardex de una variante en un almacén con saldos")
    @ApiResponse(responseCode = "200", description = "Movimientos kardex valorizados")
    @GetMapping("/kardex-producto")
    public ResponseEntity<List<KardexMovimientoDTO>> getKardexProducto(
            @Parameter(description = "ID de la variante", required = true) @RequestParam Long varianteId,
            @Parameter(description = "ID del almacén", required = true) @RequestParam Long almacenId) {
        return ResponseEntity.ok(reporteService.getKardexProducto(varianteId, almacenId));
    }

    @Operation(summary = "Movimientos por rango de fecha", description = "Lista todos los movimientos kardex en un período de tiempo")
    @ApiResponse(responseCode = "200", description = "Lista de movimientos")
    @GetMapping("/movimientos")
    public ResponseEntity<List<KardexMovimientoDTO>> getMovimientos(
            @Parameter(description = "Fecha inicio (ISO format)", example = "2026-01-01T00:00:00") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @Parameter(description = "Fecha fin (ISO format)", example = "2026-12-31T23:59:59") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(reporteService.getMovimientosPorFecha(desde, hasta));
    }

    @Operation(summary = "Productos con bajo stock", description = "Lista productos cuyo stock está por debajo del mínimo especificado")
    @ApiResponse(responseCode = "200", description = "Lista de stock bajo")
    @GetMapping("/bajo-stock")
    public ResponseEntity<List<StockDTO>> getProductosBajoStock(
            @Parameter(description = "Stock mínimo de referencia") @RequestParam(defaultValue = "10") int stockMinimo) {
        return ResponseEntity.ok(reporteService.getProductosBajoStock(stockMinimo));
    }
}
