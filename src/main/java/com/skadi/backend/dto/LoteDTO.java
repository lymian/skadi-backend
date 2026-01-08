package com.skadi.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Lote de producto para control de vencimiento y trazabilidad")
public class LoteDTO {

    @Schema(description = "ID del lote", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID de la variante padre", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long varianteId;

    @Schema(description = "Código único del lote", example = "LOTE-2026-001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El código de lote es requerido")
    private String codigoLote;

    @Schema(description = "Fecha de vencimiento del lote", example = "2027-12-31", type = "string", format = "date")
    private LocalDate fechaVencimiento;
}
