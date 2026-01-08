package com.skadi.backend.repositories;

import com.skadi.backend.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByEmpresaId(Long empresaId);

    Optional<Producto> findByIdAndEmpresaId(Long id, Long empresaId);

    List<Producto> findByEmpresaIdAndEstado(Long empresaId, String estado);

    boolean existsByCodigoAndEmpresaId(String codigo, Long empresaId);
}
