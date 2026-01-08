package com.skadi.backend.services;

import com.skadi.backend.dto.AjusteDTO;
import com.skadi.backend.entities.*;
import com.skadi.backend.exceptions.ResourceNotFoundException;
import com.skadi.backend.repositories.*;
import com.skadi.backend.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AjusteService {

        private final AjusteInventarioRepository ajusteRepository;
        private final AlmacenRepository almacenRepository;
        private final ProductoVarianteRepository varianteRepository;
        private final LoteRepository loteRepository;
        private final StockAlmacenRepository stockAlmacenRepository;
        private final KardexMovimientoRepository kardexRepository;
        private final EmpresaRepository empresaRepository;
        private final UsuarioRepository usuarioRepository;

        public List<AjusteDTO> findAll() {
                Long empresaId = TenantContext.getCurrentTenant();
                return ajusteRepository.findByEmpresaId(empresaId).stream()
                                .map(this::toDTO)
                                .collect(Collectors.toList());
        }

        @Transactional
        public AjusteDTO create(AjusteDTO dto) {
                Long empresaId = TenantContext.getCurrentTenant();

                Empresa empresa = empresaRepository.findById(empresaId)
                                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));

                Almacen almacen = almacenRepository.findByIdAndEmpresaId(dto.getAlmacenId(), empresaId)
                                .orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado"));

                ProductoVariante variante = varianteRepository.findById(dto.getVarianteId())
                                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada"));

                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                Usuario usuario = usuarioRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

                Lote lote = null;
                if (dto.getLoteId() != null) {
                        lote = loteRepository.findById(dto.getLoteId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado"));
                }

                // Obtener stock actual
                StockAlmacen stock = getOrCreateStock(empresa, almacen, variante, lote);
                int stockAntes = stock.getStock();
                int stockDespues = dto.getNuevoStock();
                int diferencia = stockDespues - stockAntes;

                // Crear ajuste
                AjusteInventario ajuste = AjusteInventario.builder()
                                .empresa(empresa)
                                .usuario(usuario)
                                .almacen(almacen)
                                .variante(variante)
                                .lote(lote)
                                .fecha(LocalDateTime.now())
                                .stockAntes(stockAntes)
                                .stockDespues(stockDespues)
                                .motivo(dto.getMotivo())
                                .build();

                ajuste = ajusteRepository.save(ajuste);

                // Actualizar stock
                stock.setStock(stockDespues);
                stockAlmacenRepository.save(stock);

                // Registrar movimiento kardex
                registrarMovimientoAjuste(empresa, almacen, variante, lote, usuario,
                                Math.abs(diferencia), stock.getCostoPromedio(), diferencia > 0, dto.getMotivo());

                return toDTO(ajuste);
        }

        private StockAlmacen getOrCreateStock(Empresa empresa, Almacen almacen,
                        ProductoVariante variante, Lote lote) {
                if (lote != null) {
                        return stockAlmacenRepository.findByAlmacenIdAndVarianteIdAndLoteId(
                                        almacen.getId(), variante.getId(), lote.getId())
                                        .orElseGet(() -> stockAlmacenRepository.save(StockAlmacen.builder()
                                                        .empresa(empresa)
                                                        .almacen(almacen)
                                                        .variante(variante)
                                                        .lote(lote)
                                                        .stock(0)
                                                        .costoPromedio(BigDecimal.ZERO)
                                                        .build()));
                } else {
                        return stockAlmacenRepository.findByAlmacenIdAndVarianteIdAndLoteIsNull(
                                        almacen.getId(), variante.getId())
                                        .orElseGet(() -> stockAlmacenRepository.save(StockAlmacen.builder()
                                                        .empresa(empresa)
                                                        .almacen(almacen)
                                                        .variante(variante)
                                                        .stock(0)
                                                        .costoPromedio(BigDecimal.ZERO)
                                                        .build()));
                }
        }

        private void registrarMovimientoAjuste(Empresa empresa, Almacen almacen,
                        ProductoVariante variante, Lote lote,
                        Usuario usuario, int cantidad,
                        BigDecimal costoPromedio, boolean esEntrada,
                        String motivo) {
                if (cantidad == 0)
                        return;

                // Obtener último saldo
                var ultimoMovimiento = lote != null
                                ? kardexRepository.findLastMovimientoByLote(variante.getId(), almacen.getId(),
                                                lote.getId())
                                : kardexRepository.findLastMovimiento(variante.getId(), almacen.getId());

                int saldoAnterior = ultimoMovimiento.map(KardexMovimiento::getSaldoCantidad).orElse(0);
                int nuevoSaldo = esEntrada ? saldoAnterior + cantidad : saldoAnterior - cantidad;
                BigDecimal costoTotal = costoPromedio.multiply(BigDecimal.valueOf(cantidad));
                BigDecimal saldoCostoTotal = costoPromedio.multiply(BigDecimal.valueOf(nuevoSaldo));

                KardexMovimiento movimiento = KardexMovimiento.builder()
                                .empresa(empresa)
                                .almacen(almacen)
                                .variante(variante)
                                .lote(lote)
                                .usuario(usuario)
                                .fecha(LocalDateTime.now())
                                .tipo("ajuste")
                                .cantidad(cantidad)
                                .costoUnitario(costoPromedio)
                                .costoTotal(costoTotal)
                                .saldoCantidad(nuevoSaldo)
                                .saldoCostoUnitario(costoPromedio)
                                .saldoCostoTotal(saldoCostoTotal)
                                .referencia("ajuste:" + motivo)
                                .build();

                kardexRepository.save(movimiento);
        }

        private AjusteDTO toDTO(AjusteInventario ajuste) {
                return AjusteDTO.builder()
                                .id(ajuste.getId())
                                .fecha(ajuste.getFecha())
                                .almacenId(ajuste.getAlmacen().getId())
                                .almacenNombre(ajuste.getAlmacen().getNombre())
                                .varianteId(ajuste.getVariante().getId())
                                .varianteNombre(ajuste.getVariante().getNombre())
                                .loteId(ajuste.getLote() != null ? ajuste.getLote().getId() : null)
                                .codigoLote(ajuste.getLote() != null ? ajuste.getLote().getCodigoLote() : null)
                                .stockAntes(ajuste.getStockAntes())
                                .nuevoStock(ajuste.getStockDespues())
                                .motivo(ajuste.getMotivo())
                                .build();
        }
}
