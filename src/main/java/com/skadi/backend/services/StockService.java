package com.skadi.backend.services;

import com.skadi.backend.dto.StockDTO;
import com.skadi.backend.entities.StockAlmacen;
import com.skadi.backend.repositories.StockAlmacenRepository;
import com.skadi.backend.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockAlmacenRepository stockAlmacenRepository;

    public List<StockDTO> findAll() {
        Long empresaId = TenantContext.getCurrentTenant();
        return stockAlmacenRepository.findByEmpresaId(empresaId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<StockDTO> findByAlmacen(Long almacenId) {
        Long empresaId = TenantContext.getCurrentTenant();
        return stockAlmacenRepository.findByEmpresaIdAndAlmacenId(empresaId, almacenId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<StockDTO> findByVariante(Long varianteId) {
        Long empresaId = TenantContext.getCurrentTenant();
        return stockAlmacenRepository.findByEmpresaIdAndVarianteId(empresaId, varianteId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private StockDTO toDTO(StockAlmacen stock) {
        BigDecimal valorTotal = stock.getCostoPromedio()
                .multiply(BigDecimal.valueOf(stock.getStock()));

        return StockDTO.builder()
                .almacenId(stock.getAlmacen().getId())
                .almacenNombre(stock.getAlmacen().getNombre())
                .varianteId(stock.getVariante().getId())
                .varianteNombre(stock.getVariante().getNombre())
                .productoNombre(stock.getVariante().getProducto().getNombre())
                .sku(stock.getVariante().getSku())
                .loteId(stock.getLote() != null ? stock.getLote().getId() : null)
                .codigoLote(stock.getLote() != null ? stock.getLote().getCodigoLote() : null)
                .stock(stock.getStock())
                .costoPromedio(stock.getCostoPromedio())
                .valorTotal(valorTotal)
                .build();
    }
}
