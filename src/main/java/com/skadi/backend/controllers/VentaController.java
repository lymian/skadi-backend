package com.skadi.backend.controllers;

import com.skadi.backend.dto.VentaDTO;
import com.skadi.backend.services.VentaService;
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
@RequestMapping("/ventas")
@RequiredArgsConstructor
@Tag(name = "Ventas", description = "Registro de salidas de inventario (ventas)")
public class VentaController {

    private final VentaService ventaService;

    @Operation(summary = "Listar todas las ventas", description = "Obtiene todas las ventas de la empresa")
    @ApiResponse(responseCode = "200", description = "Lista de ventas")
    @GetMapping
    public ResponseEntity<List<VentaDTO>> findAll() {
        return ResponseEntity.ok(ventaService.findAll());
    }

    @Operation(summary = "Obtener venta por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta encontrada"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<VentaDTO> findById(
            @Parameter(description = "ID de la venta") @PathVariable Long id) {
        return ResponseEntity.ok(ventaService.findById(id));
    }

    @Operation(summary = "Registrar nueva venta", description = "Crea venta usando FIFO (lote más antiguo primero), descuenta stock y registra movimiento kardex")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta registrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente o datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Almacén o variante no encontrada")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<VentaDTO> create(@Valid @RequestBody VentaDTO dto) {
        return ResponseEntity.ok(ventaService.create(dto));
    }
}
