package com.skadi.backend.controllers;

import com.skadi.backend.dto.StockDTO;
import com.skadi.backend.services.StockService;
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
@RequestMapping("/stock")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "Consulta de stock actual por almacén y variante")
public class StockController {

    private final StockService stockService;

    @Operation(summary = "Consultar stock", description = "Obtiene el stock actual. Puede filtrarse por almacén o por variante.")
    @ApiResponse(responseCode = "200", description = "Lista de stock con valorización")
    @GetMapping
    public ResponseEntity<List<StockDTO>> findStock(
            @Parameter(description = "Filtrar por ID de almacén") @RequestParam(required = false) Long almacenId,
            @Parameter(description = "Filtrar por ID de variante") @RequestParam(required = false) Long varianteId) {

        if (almacenId != null) {
            return ResponseEntity.ok(stockService.findByAlmacen(almacenId));
        } else if (varianteId != null) {
            return ResponseEntity.ok(stockService.findByVariante(varianteId));
        } else {
            return ResponseEntity.ok(stockService.findAll());
        }
    }
}
