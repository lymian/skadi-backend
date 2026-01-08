package com.skadi.backend.services;

import com.skadi.backend.dto.LoteDTO;
import com.skadi.backend.entities.Lote;
import com.skadi.backend.entities.ProductoVariante;
import com.skadi.backend.exceptions.ResourceNotFoundException;
import com.skadi.backend.repositories.LoteRepository;
import com.skadi.backend.repositories.ProductoVarianteRepository;
import com.skadi.backend.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoteService {

    private final LoteRepository loteRepository;
    private final ProductoVarianteRepository varianteRepository;

    public List<LoteDTO> findByVarianteId(Long varianteId) {
        ProductoVariante variante = getVarianteWithTenantCheck(varianteId);
        return loteRepository.findByVarianteId(variante.getId()).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public LoteDTO findById(Long id) {
        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado"));

        // Verificar tenant
        Long empresaId = TenantContext.getCurrentTenant();
        if (!lote.getVariante().getProducto().getEmpresa().getId().equals(empresaId)) {
            throw new ResourceNotFoundException("Lote no encontrado");
        }

        return toDTO(lote);
    }

    @Transactional
    public LoteDTO create(Long varianteId, LoteDTO dto) {
        ProductoVariante variante = getVarianteWithTenantCheck(varianteId);

        Lote lote = Lote.builder()
                .variante(variante)
                .codigoLote(dto.getCodigoLote())
                .fechaVencimiento(dto.getFechaVencimiento())
                .build();

        lote = loteRepository.save(lote);
        return toDTO(lote);
    }

    @Transactional
    public LoteDTO update(Long id, LoteDTO dto) {
        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado"));

        // Verificar tenant
        Long empresaId = TenantContext.getCurrentTenant();
        if (!lote.getVariante().getProducto().getEmpresa().getId().equals(empresaId)) {
            throw new ResourceNotFoundException("Lote no encontrado");
        }

        lote.setCodigoLote(dto.getCodigoLote());
        lote.setFechaVencimiento(dto.getFechaVencimiento());

        lote = loteRepository.save(lote);
        return toDTO(lote);
    }

    @Transactional
    public void delete(Long id) {
        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado"));

        // Verificar tenant
        Long empresaId = TenantContext.getCurrentTenant();
        if (!lote.getVariante().getProducto().getEmpresa().getId().equals(empresaId)) {
            throw new ResourceNotFoundException("Lote no encontrado");
        }

        loteRepository.delete(lote);
    }

    public List<LoteDTO> findLotesPorVencer(int dias) {
        Long empresaId = TenantContext.getCurrentTenant();
        LocalDate fechaLimite = LocalDate.now().plusDays(dias);
        return loteRepository.findLotesPorVencer(empresaId, fechaLimite).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private ProductoVariante getVarianteWithTenantCheck(Long varianteId) {
        ProductoVariante variante = varianteRepository.findById(varianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada"));

        Long empresaId = TenantContext.getCurrentTenant();
        if (!variante.getProducto().getEmpresa().getId().equals(empresaId)) {
            throw new ResourceNotFoundException("Variante no encontrada");
        }

        return variante;
    }

    private LoteDTO toDTO(Lote lote) {
        return LoteDTO.builder()
                .id(lote.getId())
                .varianteId(lote.getVariante().getId())
                .codigoLote(lote.getCodigoLote())
                .fechaVencimiento(lote.getFechaVencimiento())
                .build();
    }
}
