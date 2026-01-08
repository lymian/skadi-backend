package com.skadi.backend.controllers;

import com.skadi.backend.dto.ProductoDTO;
import com.skadi.backend.services.ProductoService;
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
@RequestMapping("/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Gestión de productos base")
public class ProductoController {

    private final ProductoService productoService;

    @Operation(summary = "Listar todos los productos", description = "Obtiene todos los productos de la empresa actual")
    @ApiResponse(responseCode = "200", description = "Lista de productos")
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> findAll() {
        return ResponseEntity.ok(productoService.findAll());
    }

    @Operation(summary = "Obtener producto por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> findById(
            @Parameter(description = "ID del producto") @PathVariable Long id) {
        return ResponseEntity.ok(productoService.findById(id));
    }

    @Operation(summary = "Crear nuevo producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto creado"),
            @ApiResponse(responseCode = "400", description = "Código duplicado o datos inválidos")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<ProductoDTO> create(@Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.ok(productoService.create(dto));
    }

    @Operation(summary = "Actualizar producto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<ProductoDTO> update(
            @Parameter(description = "ID del producto") @PathVariable Long id,
            @Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.ok(productoService.update(id, dto));
    }

    @Operation(summary = "Eliminar producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del producto") @PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
