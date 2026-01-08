package com.skadi.backend.services;

import com.skadi.backend.dto.AlmacenDTO;
import com.skadi.backend.entities.Almacen;
import com.skadi.backend.entities.Empresa;
import com.skadi.backend.exceptions.ResourceNotFoundException;
import com.skadi.backend.repositories.AlmacenRepository;
import com.skadi.backend.repositories.EmpresaRepository;
import com.skadi.backend.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlmacenService {

    private final AlmacenRepository almacenRepository;
    private final EmpresaRepository empresaRepository;

    public List<AlmacenDTO> findAll() {
        Long empresaId = TenantContext.getCurrentTenant();
        return almacenRepository.findByEmpresaId(empresaId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AlmacenDTO findById(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Almacen almacen = almacenRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado"));
        return toDTO(almacen);
    }

    @Transactional
    public AlmacenDTO create(AlmacenDTO dto) {
        Long empresaId = TenantContext.getCurrentTenant();
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));

        Almacen almacen = Almacen.builder()
                .empresa(empresa)
                .nombre(dto.getNombre())
                .direccion(dto.getDireccion())
                .estado(dto.getEstado() != null ? dto.getEstado() : "activo")
                .build();

        almacen = almacenRepository.save(almacen);
        return toDTO(almacen);
    }

    @Transactional
    public AlmacenDTO update(Long id, AlmacenDTO dto) {
        Long empresaId = TenantContext.getCurrentTenant();
        Almacen almacen = almacenRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado"));

        almacen.setNombre(dto.getNombre());
        almacen.setDireccion(dto.getDireccion());
        if (dto.getEstado() != null) {
            almacen.setEstado(dto.getEstado());
        }

        almacen = almacenRepository.save(almacen);
        return toDTO(almacen);
    }

    @Transactional
    public void delete(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Almacen almacen = almacenRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado"));
        almacenRepository.delete(almacen);
    }

    private AlmacenDTO toDTO(Almacen almacen) {
        return AlmacenDTO.builder()
                .id(almacen.getId())
                .nombre(almacen.getNombre())
                .direccion(almacen.getDireccion())
                .estado(almacen.getEstado())
                .build();
    }
}
