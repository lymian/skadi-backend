package com.skadi.backend.controllers;

import com.skadi.backend.dto.KardexMovimientoDTO;
import com.skadi.backend.services.KardexService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/kardex")
@RequiredArgsConstructor
@Tag(name = "Kardex", description = "Consulta de movimientos kardex valorizado")
public class KardexController {

    private final KardexService kardexService;

    @Operation(summary = "Consultar kardex valorizado", description = "Obtiene los movimientos kardex de una variante en un almacén, opcionalmente filtrado por lote. "
            +
            "Incluye entradas, salidas, ajustes y saldos después de cada movimiento.")
    @ApiResponse(responseCode = "200", description = "Lista de movimientos kardex con saldos")
    @GetMapping
    public ResponseEntity<List<KardexMovimientoDTO>> findKardex(
            @Parameter(description = "ID de la variante de producto", required = true) @RequestParam Long varianteId,
            @Parameter(description = "ID del almacén", required = true) @RequestParam Long almacenId,
            @Parameter(description = "ID del lote (opcional, para filtrar por lote específico)") @RequestParam(required = false) Long loteId) {
        return ResponseEntity.ok(kardexService.findKardex(varianteId, almacenId, loteId));
    }
}
