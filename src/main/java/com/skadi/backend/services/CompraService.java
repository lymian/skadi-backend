package com.skadi.backend.services;

import com.skadi.backend.dto.CompraDTO;
import com.skadi.backend.dto.CompraDetalleDTO;
import com.skadi.backend.entities.*;
import com.skadi.backend.exceptions.BadRequestException;
import com.skadi.backend.exceptions.ResourceNotFoundException;
import com.skadi.backend.repositories.*;
import com.skadi.backend.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompraService {

        private final CompraRepository compraRepository;
        private final CompraDetalleRepository compraDetalleRepository;
        private final AlmacenRepository almacenRepository;
        private final ProductoVarianteRepository varianteRepository;
        private final LoteRepository loteRepository;
        private final StockAlmacenRepository stockAlmacenRepository;
        private final KardexMovimientoRepository kardexRepository;
        private final EmpresaRepository empresaRepository;
        private final UsuarioRepository usuarioRepository;

        public List<CompraDTO> findAll() {
                Long empresaId = TenantContext.getCurrentTenant();
                return compraRepository.findByEmpresaId(empresaId).stream()
                                .map(this::toDTO)
                                .collect(Collectors.toList());
        }

        public CompraDTO findById(Long id) {
                Long empresaId = TenantContext.getCurrentTenant();
                Compra compra = compraRepository.findByIdAndEmpresaId(id, empresaId)
                                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada"));
                return toDTO(compra);
        }

        @Transactional
        public CompraDTO create(CompraDTO dto) {
                Long empresaId = TenantContext.getCurrentTenant();

                Empresa empresa = empresaRepository.findById(empresaId)
                                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));

                Almacen almacen = almacenRepository.findByIdAndEmpresaId(dto.getAlmacenId(), empresaId)
                                .orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado"));

                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                Usuario usuario = usuarioRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

                // Crear compra
                Compra compra = Compra.builder()
                                .empresa(empresa)
                                .usuario(usuario)
                                .almacen(almacen)
                                .proveedor(dto.getProveedor())
                                .fecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now())
                                .numeroDocumento(dto.getNumeroDocumento())
                                .detalles(new ArrayList<>())
                                .build();

                BigDecimal totalCompra = BigDecimal.ZERO;

                // Procesar detalles
                for (CompraDetalleDTO detalleDTO : dto.getDetalles()) {
                        ProductoVariante variante = varianteRepository.findById(detalleDTO.getVarianteId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Variante no encontrada: " + detalleDTO.getVarianteId()));

                        // Manejar lote
                        Lote lote = null;
                        boolean requiereLote = Boolean.TRUE.equals(variante.getProducto().getRequiereLote());

                        if (detalleDTO.getLoteId() != null) {
                                lote = loteRepository.findById(detalleDTO.getLoteId())
                                                .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado"));
                        } else if (detalleDTO.getCodigoLoteNuevo() != null
                                        && !detalleDTO.getCodigoLoteNuevo().isEmpty()) {
                                // Validar fecha de vencimiento si el producto requiere lote
                                if (requiereLote && detalleDTO.getFechaVencimientoLote() == null) {
                                        throw new BadRequestException(
                                                        String.format("El producto '%s' requiere especificar fecha de vencimiento del lote",
                                                                        variante.getProducto().getNombre()));
                                }

                                // Crear nuevo lote
                                lote = Lote.builder()
                                                .variante(variante)
                                                .codigoLote(detalleDTO.getCodigoLoteNuevo())
                                                .fechaVencimiento(detalleDTO.getFechaVencimientoLote())
                                                .build();
                                lote = loteRepository.save(lote);
                        }

                        // Validar si el producto requiere lote
                        if (requiereLote && lote == null) {
                                throw new BadRequestException(
                                                String.format("El producto '%s' requiere especificar lote",
                                                                variante.getProducto().getNombre()));
                        }

                        BigDecimal costoTotal = detalleDTO.getCostoUnitario()
                                        .multiply(BigDecimal.valueOf(detalleDTO.getCantidad()));

                        CompraDetalle detalle = CompraDetalle.builder()
                                        .compra(compra)
                                        .variante(variante)
                                        .lote(lote)
                                        .cantidad(detalleDTO.getCantidad())
                                        .costoUnitario(detalleDTO.getCostoUnitario())
                                        .costoTotal(costoTotal)
                                        .build();

                        compra.getDetalles().add(detalle);
                        totalCompra = totalCompra.add(costoTotal);

                        // Actualizar stock
                        StockAlmacen stock = getOrCreateStock(empresa, almacen, variante, lote);
                        updateStockEntrada(stock, detalleDTO.getCantidad(), detalleDTO.getCostoUnitario());

                        // Registrar movimiento kardex
                        registrarMovimientoEntrada(empresa, almacen, variante, lote, usuario,
                                        detalleDTO.getCantidad(), detalleDTO.getCostoUnitario(), "compra");
                }

                compra.setTotal(totalCompra);
                compra = compraRepository.save(compra);

                return toDTO(compra);
        }

        private StockAlmacen getOrCreateStock(Empresa empresa, Almacen almacen,
                        ProductoVariante variante, Lote lote) {
                if (lote != null) {
                        return stockAlmacenRepository.findByAlmacenIdAndVarianteIdAndLoteId(
                                        almacen.getId(), variante.getId(), lote.getId())
                                        .orElseGet(() -> StockAlmacen.builder()
                                                        .empresa(empresa)
                                                        .almacen(almacen)
                                                        .variante(variante)
                                                        .lote(lote)
                                                        .stock(0)
                                                        .costoPromedio(BigDecimal.ZERO)
                                                        .build());
                } else {
                        return stockAlmacenRepository.findByAlmacenIdAndVarianteIdAndLoteIsNull(
                                        almacen.getId(), variante.getId())
                                        .orElseGet(() -> StockAlmacen.builder()
                                                        .empresa(empresa)
                                                        .almacen(almacen)
                                                        .variante(variante)
                                                        .stock(0)
                                                        .costoPromedio(BigDecimal.ZERO)
                                                        .build());
                }
        }

        private void updateStockEntrada(StockAlmacen stock, int cantidad, BigDecimal costoUnitario) {
                // Cálculo de costo promedio ponderado
                BigDecimal stockActual = BigDecimal.valueOf(stock.getStock());
                BigDecimal costoActual = stock.getCostoPromedio().multiply(stockActual);
                BigDecimal nuevaCantidad = BigDecimal.valueOf(cantidad);
                BigDecimal nuevoCosto = costoUnitario.multiply(nuevaCantidad);

                BigDecimal totalCantidad = stockActual.add(nuevaCantidad);
                BigDecimal totalCosto = costoActual.add(nuevoCosto);

                BigDecimal nuevoCostoPromedio = totalCantidad.compareTo(BigDecimal.ZERO) > 0
                                ? totalCosto.divide(totalCantidad, 2, RoundingMode.HALF_UP)
                                : BigDecimal.ZERO;

                stock.setStock(stock.getStock() + cantidad);
                stock.setCostoPromedio(nuevoCostoPromedio);
                stockAlmacenRepository.save(stock);
        }

        private void registrarMovimientoEntrada(Empresa empresa, Almacen almacen,
                        ProductoVariante variante, Lote lote,
                        Usuario usuario, int cantidad,
                        BigDecimal costoUnitario, String referenciaTipo) {
                // Obtener último saldo
                var ultimoMovimiento = lote != null
                                ? kardexRepository.findLastMovimientoByLote(variante.getId(), almacen.getId(),
                                                lote.getId())
                                : kardexRepository.findLastMovimiento(variante.getId(), almacen.getId());

                int saldoAnterior = ultimoMovimiento.map(KardexMovimiento::getSaldoCantidad).orElse(0);
                BigDecimal costoPromedioAnterior = ultimoMovimiento
                                .map(KardexMovimiento::getSaldoCostoUnitario)
                                .orElse(BigDecimal.ZERO);

                int nuevoSaldo = saldoAnterior + cantidad;
                BigDecimal costoTotal = costoUnitario.multiply(BigDecimal.valueOf(cantidad));

                // Calcular nuevo costo promedio
                BigDecimal totalAnterior = costoPromedioAnterior.multiply(BigDecimal.valueOf(saldoAnterior));
                BigDecimal nuevoCostoPromedio = nuevoSaldo > 0
                                ? totalAnterior.add(costoTotal).divide(BigDecimal.valueOf(nuevoSaldo), 2,
                                                RoundingMode.HALF_UP)
                                : BigDecimal.ZERO;
                BigDecimal saldoCostoTotal = nuevoCostoPromedio.multiply(BigDecimal.valueOf(nuevoSaldo));

                KardexMovimiento movimiento = KardexMovimiento.builder()
                                .empresa(empresa)
                                .almacen(almacen)
                                .variante(variante)
                                .lote(lote)
                                .usuario(usuario)
                                .fecha(LocalDateTime.now())
                                .tipo("entrada")
                                .cantidad(cantidad)
                                .costoUnitario(costoUnitario)
                                .costoTotal(costoTotal)
                                .saldoCantidad(nuevoSaldo)
                                .saldoCostoUnitario(nuevoCostoPromedio)
                                .saldoCostoTotal(saldoCostoTotal)
                                .referencia(referenciaTipo)
                                .build();

                kardexRepository.save(movimiento);
        }

        private CompraDTO toDTO(Compra compra) {
                List<CompraDetalleDTO> detalles = compra.getDetalles().stream()
                                .map(d -> CompraDetalleDTO.builder()
                                                .id(d.getId())
                                                .varianteId(d.getVariante().getId())
                                                .loteId(d.getLote() != null ? d.getLote().getId() : null)
                                                .cantidad(d.getCantidad())
                                                .costoUnitario(d.getCostoUnitario())
                                                .build())
                                .collect(Collectors.toList());

                return CompraDTO.builder()
                                .id(compra.getId())
                                .proveedor(compra.getProveedor())
                                .fecha(compra.getFecha())
                                .numeroDocumento(compra.getNumeroDocumento())
                                .almacenId(compra.getAlmacen().getId())
                                .total(compra.getTotal())
                                .detalles(detalles)
                                .build();
        }
}
