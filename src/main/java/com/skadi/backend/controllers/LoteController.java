package com.skadi.backend.controllers;

import com.skadi.backend.dto.LoteDTO;
import com.skadi.backend.services.LoteService;
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
@Tag(name = "Lotes", description = "Gestión de lotes con fecha de vencimiento")
public class LoteController {

    private final LoteService loteService;

    @Operation(summary = "Listar lotes de una variante")
    @ApiResponse(responseCode = "200", description = "Lista de lotes")
    @GetMapping("/variantes/{varianteId}/lotes")
    public ResponseEntity<List<LoteDTO>> findByVarianteId(
            @Parameter(description = "ID de la variante") @PathVariable Long varianteId) {
        return ResponseEntity.ok(loteService.findByVarianteId(varianteId));
    }

    @Operation(summary = "Crear lote para una variante")
    @PostMapping("/variantes/{varianteId}/lotes")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<LoteDTO> create(
            @Parameter(description = "ID de la variante") @PathVariable Long varianteId,
            @Valid @RequestBody LoteDTO dto) {
        return ResponseEntity.ok(loteService.create(varianteId, dto));
    }

    @Operation(summary = "Obtener lote por ID")
    @GetMapping("/lotes/{id}")
    public ResponseEntity<LoteDTO> findById(
            @Parameter(description = "ID del lote") @PathVariable Long id) {
        return ResponseEntity.ok(loteService.findById(id));
    }

    @Operation(summary = "Actualizar lote")
    @PutMapping("/lotes/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<LoteDTO> update(
            @Parameter(description = "ID del lote") @PathVariable Long id,
            @Valid @RequestBody LoteDTO dto) {
        return ResponseEntity.ok(loteService.update(id, dto));
    }

    @Operation(summary = "Eliminar lote")
    @DeleteMapping("/lotes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del lote") @PathVariable Long id) {
        loteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
