package com.skadi.backend.services;

import com.skadi.backend.dto.KardexMovimientoDTO;
import com.skadi.backend.dto.LoteDTO;
import com.skadi.backend.dto.StockDTO;
import com.skadi.backend.entities.Lote;
import com.skadi.backend.entities.StockAlmacen;
import com.skadi.backend.repositories.KardexMovimientoRepository;
import com.skadi.backend.repositories.LoteRepository;
import com.skadi.backend.repositories.StockAlmacenRepository;
import com.skadi.backend.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final StockAlmacenRepository stockAlmacenRepository;
    private final KardexMovimientoRepository kardexRepository;
    private final LoteRepository loteRepository;

    /**
     * Stock general por almacén
     */
    public List<StockDTO> getStockGeneral() {
        Long empresaId = TenantContext.getCurrentTenant();
        return stockAlmacenRepository.findByEmpresaId(empresaId).stream()
                .map(this::stockToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Valorización del inventario
     */
    public Map<String, Object> getValorizacion() {
        Long empresaId = TenantContext.getCurrentTenant();
        List<StockAlmacen> stocks = stockAlmacenRepository.findByEmpresaId(empresaId);

        BigDecimal valorTotal = BigDecimal.ZERO;
        int totalUnidades = 0;
        List<StockDTO> detalle = stocks.stream().map(this::stockToDTO).collect(Collectors.toList());

        for (StockAlmacen stock : stocks) {
            valorTotal = valorTotal.add(
                    stock.getCostoPromedio().multiply(BigDecimal.valueOf(stock.getStock())));
            totalUnidades += stock.getStock();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("valorTotal", valorTotal);
        result.put("totalUnidades", totalUnidades);
        result.put("detalle", detalle);
        return result;
    }

    /**
     * Lotes por vencer en los próximos días
     */
    public List<LoteDTO> getLotesPorVencer(int dias) {
        Long empresaId = TenantContext.getCurrentTenant();
        LocalDate fechaLimite = LocalDate.now().plusDays(dias);
        return loteRepository.findLotesPorVencer(empresaId, fechaLimite).stream()
                .map(this::loteToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Kardex de un producto específico
     */
    public List<KardexMovimientoDTO> getKardexProducto(Long varianteId, Long almacenId) {
        Long empresaId = TenantContext.getCurrentTenant();
        return kardexRepository.findKardex(empresaId, varianteId, almacenId).stream()
                .map(mov -> KardexMovimientoDTO.builder()
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
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Movimientos por rango de fecha
     */
    public List<KardexMovimientoDTO> getMovimientosPorFecha(LocalDateTime desde, LocalDateTime hasta) {
        Long empresaId = TenantContext.getCurrentTenant();
        return kardexRepository.findByEmpresaIdAndFechaBetween(empresaId, desde, hasta).stream()
                .map(mov -> KardexMovimientoDTO.builder()
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
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Productos con stock debajo del mínimo (placeholder - requiere campo de stock
     * mínimo)
     */
    public List<StockDTO> getProductosBajoStock(int stockMinimo) {
        Long empresaId = TenantContext.getCurrentTenant();
        return stockAlmacenRepository.findByEmpresaId(empresaId).stream()
                .filter(s -> s.getStock() < stockMinimo)
                .map(this::stockToDTO)
                .collect(Collectors.toList());
    }

    private StockDTO stockToDTO(StockAlmacen stock) {
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

    private LoteDTO loteToDTO(Lote lote) {
        return LoteDTO.builder()
                .id(lote.getId())
                .varianteId(lote.getVariante().getId())
                .codigoLote(lote.getCodigoLote())
                .fechaVencimiento(lote.getFechaVencimiento())
                .build();
    }
}
