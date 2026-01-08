package com.skadi.backend.controllers;

import com.skadi.backend.dto.AjusteDTO;
import com.skadi.backend.services.AjusteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ajustes")
@RequiredArgsConstructor
@Tag(name = "Ajustes", description = "Ajustes de inventario (correcciones, pérdidas, inventario físico)")
public class AjusteController {

    private final AjusteService ajusteService;

    @Operation(summary = "Listar todos los ajustes")
    @ApiResponse(responseCode = "200", description = "Lista de ajustes realizados")
    @GetMapping
    public ResponseEntity<List<AjusteDTO>> findAll() {
        return ResponseEntity.ok(ajusteService.findAll());
    }

    @Operation(summary = "Crear ajuste de inventario", description = "Ajusta el stock de una variante a un nuevo valor y registra el movimiento kardex")
    @ApiResponse(responseCode = "200", description = "Ajuste registrado")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<AjusteDTO> create(@Valid @RequestBody AjusteDTO dto) {
        return ResponseEntity.ok(ajusteService.create(dto));
    }
}
