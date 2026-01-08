package com.skadi.backend.services;

import com.skadi.backend.dto.EmpresaDTO;
import com.skadi.backend.entities.Empresa;
import com.skadi.backend.exceptions.ResourceNotFoundException;
import com.skadi.backend.repositories.EmpresaRepository;
import com.skadi.backend.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaDTO getMyEmpresa() {
        Long empresaId = TenantContext.getCurrentTenant();
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));
        return toDTO(empresa);
    }

    @Transactional
    public EmpresaDTO updateMyEmpresa(EmpresaDTO dto) {
        Long empresaId = TenantContext.getCurrentTenant();
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));

        // Actualizar campos
        if (dto.getNombre() != null)
            empresa.setNombre(dto.getNombre());
        if (dto.getRuc() != null)
            empresa.setRuc(dto.getRuc());
        if (dto.getDireccion() != null)
            empresa.setDireccion(dto.getDireccion());
        if (dto.getTelefono() != null)
            empresa.setTelefono(dto.getTelefono());
        if (dto.getEmail() != null)
            empresa.setEmail(dto.getEmail());

        empresa = empresaRepository.save(empresa);
        return toDTO(empresa);
    }

    private EmpresaDTO toDTO(Empresa empresa) {
        return EmpresaDTO.builder()
                .id(empresa.getId())
                .nombre(empresa.getNombre())
                .ruc(empresa.getRuc())
                .direccion(empresa.getDireccion())
                .telefono(empresa.getTelefono())
                .email(empresa.getEmail())
                .createdAt(empresa.getCreatedAt())
                .build();
    }
}
