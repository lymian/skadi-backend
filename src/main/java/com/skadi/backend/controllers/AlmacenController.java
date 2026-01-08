package com.skadi.backend.controllers;

import com.skadi.backend.dto.AlmacenDTO;
import com.skadi.backend.services.AlmacenService;
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
@RequestMapping("/almacenes")
@RequiredArgsConstructor
@Tag(name = "Almacenes", description = "Gestión de almacenes/bodegas")
public class AlmacenController {

    private final AlmacenService almacenService;

    @Operation(summary = "Listar todos los almacenes")
    @ApiResponse(responseCode = "200", description = "Lista de almacenes")
    @GetMapping
    public ResponseEntity<List<AlmacenDTO>> findAll() {
        return ResponseEntity.ok(almacenService.findAll());
    }

    @Operation(summary = "Obtener almacén por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Almacén encontrado"),
            @ApiResponse(responseCode = "404", description = "Almacén no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlmacenDTO> findById(
            @Parameter(description = "ID del almacén") @PathVariable Long id) {
        return ResponseEntity.ok(almacenService.findById(id));
    }

    @Operation(summary = "Crear nuevo almacén")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<AlmacenDTO> create(@Valid @RequestBody AlmacenDTO dto) {
        return ResponseEntity.ok(almacenService.create(dto));
    }

    @Operation(summary = "Actualizar almacén")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<AlmacenDTO> update(
            @Parameter(description = "ID del almacén") @PathVariable Long id,
            @Valid @RequestBody AlmacenDTO dto) {
        return ResponseEntity.ok(almacenService.update(id, dto));
    }

    @Operation(summary = "Eliminar almacén")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del almacén") @PathVariable Long id) {
        almacenService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
