package com.skadi.backend.services;

import com.skadi.backend.dto.UsuarioDTO;
import com.skadi.backend.entities.Empresa;
import com.skadi.backend.entities.Usuario;
import com.skadi.backend.exceptions.BadRequestException;
import com.skadi.backend.exceptions.ResourceNotFoundException;
import com.skadi.backend.repositories.EmpresaRepository;
import com.skadi.backend.repositories.UsuarioRepository;
import com.skadi.backend.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioDTO> findAll() {
        Long empresaId = TenantContext.getCurrentTenant();
        return usuarioRepository.findByEmpresaId(empresaId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UsuarioDTO findById(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Usuario usuario = usuarioRepository.findById(id)
                .filter(u -> u.getEmpresa().getId().equals(empresaId))
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return toDTO(usuario);
    }

    @Transactional
    public UsuarioDTO create(UsuarioDTO dto) {
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new BadRequestException("El username ya existe");
        }

        Long empresaId = TenantContext.getCurrentTenant();
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));

        Usuario usuario = Usuario.builder()
                .empresa(empresa)
                .username(dto.getUsername())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .rol(dto.getRol() != null ? dto.getRol() : "consulta")
                .build();

        usuario = usuarioRepository.save(usuario);
        return toDTO(usuario);
    }

    @Transactional
    public UsuarioDTO update(Long id, UsuarioDTO dto) {
        Long empresaId = TenantContext.getCurrentTenant();
        Usuario usuario = usuarioRepository.findById(id)
                .filter(u -> u.getEmpresa().getId().equals(empresaId))
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Verificar username duplicado si cambió
        if (!usuario.getUsername().equals(dto.getUsername()) &&
                usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new BadRequestException("El username ya existe");
        }

        usuario.setUsername(dto.getUsername());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getRol() != null) {
            usuario.setRol(dto.getRol());
        }

        usuario = usuarioRepository.save(usuario);
        return toDTO(usuario);
    }

    @Transactional
    public void delete(Long id) {
        Long empresaId = TenantContext.getCurrentTenant();
        Usuario usuario = usuarioRepository.findById(id)
                .filter(u -> u.getEmpresa().getId().equals(empresaId))
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        usuarioRepository.delete(usuario);
    }

    private UsuarioDTO toDTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .rol(usuario.getRol())
                .build();
    }
}
