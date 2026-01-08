package com.skadi.backend.services;

import com.skadi.backend.dto.VentaDTO;
import com.skadi.backend.dto.VentaDetalleDTO;
import com.skadi.backend.entities.*;
import com.skadi.backend.exceptions.BadRequestException;
import com.skadi.backend.exceptions.InsufficientStockException;
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
public class VentaService {

        private final VentaRepository ventaRepository;
        private final AlmacenRepository almacenRepository;
        private final ProductoVarianteRepository varianteRepository;
        private final LoteRepository loteRepository;
        private final StockAlmacenRepository stockAlmacenRepository;
        private final KardexMovimientoRepository kardexRepository;
        private final EmpresaRepository empresaRepository;
        private final UsuarioRepository usuarioRepository;

        public List<VentaDTO> findAll() {
                Long empresaId = TenantContext.getCurrentTenant();
                return ventaRepository.findByEmpresaId(empresaId).stream()
                                .map(this::toDTO)
                                .collect(Collectors.toList());
        }

        public VentaDTO findById(Long id) {
                Long empresaId = TenantContext.getCurrentTenant();
                Venta venta = ventaRepository.findByIdAndEmpresaId(id, empresaId)
                                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));
                return toDTO(venta);
        }

        @Transactional
        public VentaDTO create(VentaDTO dto) {
                Long empresaId = TenantContext.getCurrentTenant();

                Empresa empresa = empresaRepository.findById(empresaId)
                                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));

                Almacen almacen = almacenRepository.findByIdAndEmpresaId(dto.getAlmacenId(), empresaId)
                                .orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado"));

                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                Usuario usuario = usuarioRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

                // Crear venta
                Venta venta = Venta.builder()
                                .empresa(empresa)
                                .usuario(usuario)
                                .almacen(almacen)
                                .cliente(dto.getCliente())
                                .fecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now())
                                .numeroDocumento(dto.getNumeroDocumento())
                                .detalles(new ArrayList<>())
                                .build();

                BigDecimal totalVenta = BigDecimal.ZERO;

                // Procesar detalles
                for (VentaDetalleDTO detalleDTO : dto.getDetalles()) {
                        ProductoVariante variante = varianteRepository.findById(detalleDTO.getVarianteId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Variante no encontrada: " + detalleDTO.getVarianteId()));

                        Lote lote = null;
                        if (detalleDTO.getLoteId() != null) {
                                lote = loteRepository.findById(detalleDTO.getLoteId())
                                                .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado"));
                        }

                        // Aplicar FIFO para obtener el stock
                        // Si el producto requiere lote y no se especificó, el FIFO solo usará stocks
                        // con lote
                        boolean requiereLote = Boolean.TRUE.equals(variante.getProducto().getRequiereLote());
                        procesarSalidaFIFO(empresa, almacen, variante,
                                        lote, usuario, detalleDTO.getCantidad(), requiereLote);

                        BigDecimal subtotal = detalleDTO.getPrecioUnitario()
                                        .multiply(BigDecimal.valueOf(detalleDTO.getCantidad()));

                        VentaDetalle detalle = VentaDetalle.builder()
                                        .venta(venta)
                                        .variante(variante)
                                        .lote(lote)
                                        .cantidad(detalleDTO.getCantidad())
                                        .precioUnitario(detalleDTO.getPrecioUnitario())
                                        .subtotal(subtotal)
                                        .build();

                        venta.getDetalles().add(detalle);
                        totalVenta = totalVenta.add(subtotal);
                }

                venta.setTotal(totalVenta);
                venta = ventaRepository.save(venta);

                return toDTO(venta);
        }

        /**
         * Procesa la salida usando FIFO (First In, First Out)
         * Primero sale el stock de los lotes más antiguos
         * 
         * @param requiereLote si true, solo usa stocks con lote asignado
         */
        private void procesarSalidaFIFO(Empresa empresa, Almacen almacen,
                        ProductoVariante variante, Lote loteEspecifico,
                        Usuario usuario, int cantidadRequerida, boolean requiereLote) {
                int cantidadRestante = cantidadRequerida;

                List<StockAlmacen> stocksDisponibles;

                if (loteEspecifico != null) {
                        // Si se especifica lote, usar solo ese
                        stocksDisponibles = stockAlmacenRepository
                                        .findByAlmacenIdAndVarianteIdAndLoteId(almacen.getId(), variante.getId(),
                                                        loteEspecifico.getId())
                                        .stream().toList();
                } else if (requiereLote) {
                        // Producto requiere lote: FIFO solo con stocks que tienen lote
                        stocksDisponibles = stockAlmacenRepository
                                        .findAvailableStockWithLoteFIFO(almacen.getId(), variante.getId());
                } else {
                        // FIFO normal: incluye stocks con y sin lote
                        stocksDisponibles = stockAlmacenRepository
                                        .findAvailableStockFIFO(almacen.getId(), variante.getId());
                }

                // Verificar que hay stock suficiente
                int stockTotal = stocksDisponibles.stream().mapToInt(StockAlmacen::getStock).sum();
                if (stockTotal < cantidadRequerida) {
                        throw new InsufficientStockException(
                                        String.format("Stock insuficiente para variante %s. Disponible: %d, Requerido: %d",
                                                        variante.getNombre(), stockTotal, cantidadRequerida));
                }

                // Procesar salidas FIFO
                for (StockAlmacen stock : stocksDisponibles) {
                        if (cantidadRestante <= 0)
                                break;

                        int cantidadASacar = Math.min(stock.getStock(), cantidadRestante);

                        // Actualizar stock
                        stock.setStock(stock.getStock() - cantidadASacar);
                        stockAlmacenRepository.save(stock);

                        // Registrar movimiento kardex
                        registrarMovimientoSalida(empresa, almacen, variante, stock.getLote(),
                                        usuario, cantidadASacar, stock.getCostoPromedio(), "venta");

                        cantidadRestante -= cantidadASacar;
                }
        }

        private void registrarMovimientoSalida(Empresa empresa, Almacen almacen,
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
                                .orElse(costoUnitario);

                int nuevoSaldo = saldoAnterior - cantidad;
                BigDecimal costoTotal = costoUnitario.multiply(BigDecimal.valueOf(cantidad));
                BigDecimal saldoCostoTotal = costoPromedioAnterior.multiply(BigDecimal.valueOf(nuevoSaldo));

                KardexMovimiento movimiento = KardexMovimiento.builder()
                                .empresa(empresa)
                                .almacen(almacen)
                                .variante(variante)
                                .lote(lote)
                                .usuario(usuario)
                                .fecha(LocalDateTime.now())
                                .tipo("salida")
                                .cantidad(cantidad)
                                .costoUnitario(costoUnitario)
                                .costoTotal(costoTotal)
                                .saldoCantidad(nuevoSaldo)
                                .saldoCostoUnitario(costoPromedioAnterior)
                                .saldoCostoTotal(saldoCostoTotal)
                                .referencia(referenciaTipo)
                                .build();

                kardexRepository.save(movimiento);
        }

        private VentaDTO toDTO(Venta venta) {
                List<VentaDetalleDTO> detalles = venta.getDetalles().stream()
                                .map(d -> VentaDetalleDTO.builder()
                                                .id(d.getId())
                                                .varianteId(d.getVariante().getId())
                                                .loteId(d.getLote() != null ? d.getLote().getId() : null)
                                                .cantidad(d.getCantidad())
                                                .precioUnitario(d.getPrecioUnitario())
                                                .build())
                                .collect(Collectors.toList());

                return VentaDTO.builder()
                                .id(venta.getId())
                                .cliente(venta.getCliente())
                                .fecha(venta.getFecha())
                                .numeroDocumento(venta.getNumeroDocumento())
                                .almacenId(venta.getAlmacen().getId())
                                .total(venta.getTotal())
                                .detalles(detalles)
                                .build();
        }
}
