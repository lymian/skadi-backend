package com.skadi.backend.controllers;

import com.skadi.backend.dto.EmpresaDTO;
import com.skadi.backend.services.EmpresaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/empresas")
@RequiredArgsConstructor
@Tag(name = "Empresas", description = "Gestión de la empresa actual (Tenant)")
public class EmpresaController {

    private final EmpresaService empresaService;

    @Operation(summary = "Obtener datos de mi empresa", description = "Retorna la información de la empresa asociada al token actual")
    @ApiResponse(responseCode = "200", description = "Datos de la empresa")
    @GetMapping("/me")
    public ResponseEntity<EmpresaDTO> me() {
        return ResponseEntity.ok(empresaService.getMyEmpresa());
    }

    @Operation(summary = "Actualizar datos de mi empresa", description = "Permite actualizar nombre, ruc, dirección, etc. (Solo ADMIN)")
    @ApiResponse(responseCode = "200", description = "Datos actualizados")
    @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/me")
    public ResponseEntity<EmpresaDTO> update(@Valid @RequestBody EmpresaDTO dto) {
        return ResponseEntity.ok(empresaService.updateMyEmpresa(dto));
    }
}
