package com.skadi.backend.services;

import com.skadi.backend.dto.AuthResponse;
import com.skadi.backend.dto.LoginRequest;
import com.skadi.backend.dto.RegisterRequest;
import com.skadi.backend.entities.Empresa;
import com.skadi.backend.entities.Usuario;
import com.skadi.backend.exceptions.BadRequestException;
import com.skadi.backend.repositories.EmpresaRepository;
import com.skadi.backend.repositories.UsuarioRepository;
import com.skadi.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Verificar que el username no exista
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("El username ya existe");
        }

        // Crear empresa
        Empresa empresa = Empresa.builder()
                .nombre(request.getEmpresaNombre())
                .build();
        empresa = empresaRepository.save(empresa);

        // Crear usuario admin
        String rol = request.getRol() != null ? request.getRol() : "admin";
        Usuario usuario = Usuario.builder()
                .empresa(empresa)
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .rol(rol)
                .build();
        usuario = usuarioRepository.save(usuario);

        // Generar token
        String token = jwtService.generateToken(usuario, empresa.getId());

        return AuthResponse.builder()
                .token(token)
                .empresaId(empresa.getId())
                .empresaNombre(empresa.getNombre())
                .username(usuario.getUsername())
                .rol(usuario.getRol())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        Empresa empresa = usuario.getEmpresa();
        String token = jwtService.generateToken(usuario, empresa.getId());

        return AuthResponse.builder()
                .token(token)
                .empresaId(empresa.getId())
                .empresaNombre(empresa.getNombre())
                .username(usuario.getUsername())
                .rol(usuario.getRol())
                .build();
    }

    public AuthResponse me(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        Empresa empresa = usuario.getEmpresa();

        return AuthResponse.builder()
                .empresaId(empresa.getId())
                .empresaNombre(empresa.getNombre())
                .username(usuario.getUsername())
                .rol(usuario.getRol())
                .build();
    }
}
