package com.skadi.backend.services;

import com.skadi.backend.dto.ProductoDTO;
import com.skadi.backend.dto.VarianteDTO;
import com.skadi.backend.entities.Empresa;
import com.skadi.backend.entities.Producto;
import com.skadi.backend.entities.ProductoVariante;
import com.skadi.backend.exceptions.BadRequestException;
import com.skadi.backend.exceptions.ResourceNotFoundException;
import com.skadi.backend.repositories.EmpresaRepository;
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
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoVarianteRepository varianteRepository;
    private final EmpresaRepository empresaRepository;

    public List<ProductoDTO> findAll() {
        Long empresaId = TenantContext.getCurrentTenant();
        return productoRepository.findByEmpresaId(empresaId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProductoDTO findById(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Producto producto = productoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        return toDTO(producto);
    }

    @Transactional
    public ProductoDTO create(ProductoDTO dto) {
        Long empresaId = TenantContext.getCurrentTenant();

        if (productoRepository.existsByCodigoAndEmpresaId(dto.getCodigo(), empresaId)) {
            throw new BadRequestException("Ya existe un producto con ese código");
        }

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));

        Producto producto = Producto.builder()
                .empresa(empresa)
                .codigo(dto.getCodigo())
                .nombre(dto.getNombre())
                .categoria(dto.getCategoria())
                .descripcion(dto.getDescripcion())
                .unidadBase(dto.getUnidadBase())
                .estado(dto.getEstado() != null ? dto.getEstado() : "activo")
                .requiereLote(dto.getRequiereLote() != null ? dto.getRequiereLote() : false)
                .build();

        producto = productoRepository.save(producto);

        // Auto-crear variante default con el mismo nombre del producto
        ProductoVariante varianteDefault = ProductoVariante.builder()
                .producto(producto)
                .nombre(producto.getNombre())
                .sku(producto.getCodigo() + "-DEFAULT")
                .build();
        varianteRepository.save(varianteDefault);

        // Recargar producto con la variante
        producto = productoRepository.findById(producto.getId()).orElse(producto);
        return toDTO(producto);
    }

    @Transactional
    public ProductoDTO update(Long id, ProductoDTO dto) {
        Long empresaId = TenantContext.getCurrentTenant();
        Producto producto = productoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        // Verificar código duplicado si cambió
        if (!producto.getCodigo().equals(dto.getCodigo()) &&
                productoRepository.existsByCodigoAndEmpresaId(dto.getCodigo(), empresaId)) {
            throw new BadRequestException("Ya existe un producto con ese código");
        }

        producto.setCodigo(dto.getCodigo());
        producto.setNombre(dto.getNombre());
        producto.setCategoria(dto.getCategoria());
        producto.setDescripcion(dto.getDescripcion());
        producto.setUnidadBase(dto.getUnidadBase());
        if (dto.getEstado() != null) {
            producto.setEstado(dto.getEstado());
        }
        if (dto.getRequiereLote() != null) {
            producto.setRequiereLote(dto.getRequiereLote());
        }

        producto = productoRepository.save(producto);
        return toDTO(producto);
    }

    @Transactional
    public void delete(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Producto producto = productoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        productoRepository.delete(producto);
    }

    private ProductoDTO toDTO(Producto producto) {
        List<VarianteDTO> variantes = producto.getVariantes().stream()
                .map(v -> VarianteDTO.builder()
                        .id(v.getId())
                        .productoId(producto.getId())
                        .nombre(v.getNombre())
                        .sku(v.getSku())
                        .precioCompra(v.getPrecioCompra())
                        .precioVenta(v.getPrecioVenta())
                        .build())
                .collect(Collectors.toList());

        return ProductoDTO.builder()
                .id(producto.getId())
                .codigo(producto.getCodigo())
                .nombre(producto.getNombre())
                .categoria(producto.getCategoria())
                .descripcion(producto.getDescripcion())
                .unidadBase(producto.getUnidadBase())
                .estado(producto.getEstado())
                .requiereLote(producto.getRequiereLote())
                .variantes(variantes)
                .build();
    }
}
