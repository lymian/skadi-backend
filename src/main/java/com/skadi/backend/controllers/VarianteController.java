package com.skadi.backend.controllers;

import com.skadi.backend.dto.VarianteDTO;
import com.skadi.backend.services.VarianteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Variantes", description = "Gestión de variantes de producto (talla, color, SKU)")
public class VarianteController {

    private final VarianteService varianteService;

    @Operation(summary = "Listar variantes de un producto")
    @ApiResponse(responseCode = "200", description = "Lista de variantes")
    @GetMapping("/productos/{productoId}/variantes")
    public ResponseEntity<List<VarianteDTO>> findByProductoId(
            @Parameter(description = "ID del producto") @PathVariable Long productoId) {
        return ResponseEntity.ok(varianteService.findByProductoId(productoId));
    }

    @Operation(summary = "Crear variante para un producto")
    @PostMapping("/productos/{productoId}/variantes")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<VarianteDTO> create(
            @Parameter(description = "ID del producto") @PathVariable Long productoId,
            @Valid @RequestBody VarianteDTO dto) {
        return ResponseEntity.ok(varianteService.create(productoId, dto));
    }

    @Operation(summary = "Obtener variante por ID")
    @GetMapping("/variantes/{id}")
    public ResponseEntity<VarianteDTO> findById(
            @Parameter(description = "ID de la variante") @PathVariable Long id) {
        return ResponseEntity.ok(varianteService.findById(id));
    }

    @Operation(summary = "Actualizar variante")
    @PutMapping("/variantes/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<VarianteDTO> update(
            @Parameter(description = "ID de la variante") @PathVariable Long id,
            @Valid @RequestBody VarianteDTO dto) {
        return ResponseEntity.ok(varianteService.update(id, dto));
    }

    @Operation(summary = "Eliminar variante")
    @DeleteMapping("/variantes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la variante") @PathVariable Long id) {
        varianteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
