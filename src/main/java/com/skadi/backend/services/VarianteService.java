package com.skadi.backend.services;

import com.skadi.backend.dto.VarianteDTO;
import com.skadi.backend.entities.Producto;
import com.skadi.backend.entities.ProductoVariante;
import com.skadi.backend.exceptions.BadRequestException;
import com.skadi.backend.exceptions.ResourceNotFoundException;
import com.skadi.backend.repositories.ProductoRepository;
import com.skadi.backend.repositories.ProductoVarianteRepository;
import com.skadi.backend.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VarianteService {

    private final ProductoVarianteRepository varianteRepository;
    private final ProductoRepository productoRepository;

    public List<VarianteDTO> findByProductoId(Long productoId) {
        Long empresaId = TenantContext.getCurrentTenant();
        // Verificar que el producto pertenece a la empresa
        productoRepository.findByIdAndEmpresaId(productoId, empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        return varianteRepository.findByProductoId(productoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public VarianteDTO findById(Long id) {
        ProductoVariante variante = varianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada"));

        // Verificar tenant
        Long empresaId = TenantContext.getCurrentTenant();
        if (!variante.getProducto().getEmpresa().getId().equals(empresaId)) {
            throw new ResourceNotFoundException("Variante no encontrada");
        }

        return toDTO(variante);
    }

    @Transactional
    public VarianteDTO create(Long productoId, VarianteDTO dto) {
        Long empresaId = TenantContext.getCurrentTenant();
        Producto producto = productoRepository.findByIdAndEmpresaId(productoId, empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        if (dto.getSku() != null && varianteRepository.existsBySku(dto.getSku())) {
            throw new BadRequestException("Ya existe una variante con ese SKU");
        }

        ProductoVariante variante = ProductoVariante.builder()
                .producto(producto)
                .nombre(dto.getNombre())
                .sku(dto.getSku())
                .precioCompra(dto.getPrecioCompra())
                .precioVenta(dto.getPrecioVenta())
                .build();

        variante = varianteRepository.save(variante);
        return toDTO(variante);
    }

    @Transactional
    public VarianteDTO update(Long id, VarianteDTO dto) {
        ProductoVariante variante = varianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada"));

        // Verificar tenant
        Long empresaId = TenantContext.getCurrentTenant();
        if (!variante.getProducto().getEmpresa().getId().equals(empresaId)) {
            throw new ResourceNotFoundException("Variante no encontrada");
        }

        // Verificar SKU duplicado si cambió
        if (dto.getSku() != null && !dto.getSku().equals(variante.getSku()) &&
                varianteRepository.existsBySku(dto.getSku())) {
            throw new BadRequestException("Ya existe una variante con ese SKU");
        }

        variante.setNombre(dto.getNombre());
        variante.setSku(dto.getSku());
        variante.setPrecioCompra(dto.getPrecioCompra());
        variante.setPrecioVenta(dto.getPrecioVenta());

        variante = varianteRepository.save(variante);
        return toDTO(variante);
    }

    @Transactional
    public void delete(Long id) {
        ProductoVariante variante = varianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada"));

        // Verificar tenant
        Long empresaId = TenantContext.getCurrentTenant();
        if (!variante.getProducto().getEmpresa().getId().equals(empresaId)) {
            throw new ResourceNotFoundException("Variante no encontrada");
        }

        varianteRepository.delete(variante);
    }

    private VarianteDTO toDTO(ProductoVariante variante) {
        return VarianteDTO.builder()
                .id(variante.getId())
                .productoId(variante.getProducto().getId())
                .nombre(variante.getNombre())
                .sku(variante.getSku())
                .precioCompra(variante.getPrecioCompra())
                .precioVenta(variante.getPrecioVenta())
                .build();
    }
}
