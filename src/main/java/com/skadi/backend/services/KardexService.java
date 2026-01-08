package com.skadi.backend.services;

import com.skadi.backend.dto.KardexMovimientoDTO;
import com.skadi.backend.entities.KardexMovimiento;
import com.skadi.backend.repositories.KardexMovimientoRepository;
import com.skadi.backend.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KardexService {

    private final KardexMovimientoRepository kardexRepository;

    public List<KardexMovimientoDTO> findKardex(Long varianteId, Long almacenId, Long loteId) {
        Long empresaId = TenantContext.getCurrentTenant();

        List<KardexMovimiento> movimientos;
        if (loteId != null) {
            movimientos = kardexRepository.findKardexByLote(empresaId, varianteId, almacenId, loteId);
        } else {
            movimientos = kardexRepository.findKardex(empresaId, varianteId, almacenId);
        }

        return movimientos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<KardexMovimientoDTO> findByDateRange(LocalDateTime desde, LocalDateTime hasta) {
        Long empresaId = TenantContext.getCurrentTenant();
        return kardexRepository.findByEmpresaIdAndFechaBetween(empresaId, desde, hasta).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private KardexMovimientoDTO toDTO(KardexMovimiento mov) {
        return KardexMovimientoDTO.builder()
                .id(mov.getId())
                .almacenId(mov.getAlmacen().getId())
                .almacenNombre(mov.getAlmacen().getNombre())
                .varianteId(mov.getVariante().getId())
                .varianteNombre(mov.getVariante().getNombre())
                .loteId(mov.getLote() != null ? mov.getLote().getId() : null)
                .codigoLote(mov.getLote() != null ? mov.getLote().getCodigoLote() : null)
                .fecha(mov.getFecha())
                .tipo(mov.getTipo())
                .cantidad(mov.getCantidad())
                .costoUnitario(mov.getCostoUnitario())
                .costoTotal(mov.getCostoTotal())
                .saldoCantidad(mov.getSaldoCantidad())
                .saldoCostoUnitario(mov.getSaldoCostoUnitario())
                .saldoCostoTotal(mov.getSaldoCostoTotal())
                .referencia(mov.getReferencia())
                .build();
    }
}
