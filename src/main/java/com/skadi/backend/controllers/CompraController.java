package com.skadi.backend.controllers;

import com.skadi.backend.dto.CompraDTO;
import com.skadi.backend.services.CompraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compras")
@RequiredArgsConstructor
@Tag(name = "Compras", description = "Registro de entradas de inventario (compras)")
public class CompraController {

    private final CompraService compraService;

    @Operation(summary = "Listar todas las compras", description = "Obtiene todas las compras de la empresa")
    @ApiResponse(responseCode = "200", description = "Lista de compras")
    @GetMapping
    public ResponseEntity<List<CompraDTO>> findAll() {
        return ResponseEntity.ok(compraService.findAll());
    }

    @Operation(summary = "Obtener compra por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compra encontrada"),
            @ApiResponse(responseCode = "404", description = "Compra no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CompraDTO> findById(
            @Parameter(description = "ID de la compra") @PathVariable Long id) {
        return ResponseEntity.ok(compraService.findById(id));
    }

    @Operation(summary = "Registrar nueva compra", description = "Crea compra, actualiza stock con costo promedio ponderado y registra movimiento kardex")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compra registrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Almacén o variante no encontrada")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<CompraDTO> create(@Valid @RequestBody CompraDTO dto) {
        return ResponseEntity.ok(compraService.create(dto));
    }
}
