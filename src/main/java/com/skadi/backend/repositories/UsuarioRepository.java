package com.skadi.backend.repositories;

import com.skadi.backend.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u JOIN FETCH u.empresa WHERE u.username = :username")
    Optional<Usuario> findByUsername(@Param("username") String username);

    List<Usuario> findByEmpresaId(Long empresaId);

    boolean existsByUsername(String username);
}
