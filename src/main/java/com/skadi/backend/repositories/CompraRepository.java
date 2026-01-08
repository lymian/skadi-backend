package com.skadi.backend.repositories;

import com.skadi.backend.entities.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    List<Compra> findByEmpresaId(Long empresaId);

    Optional<Compra> findByIdAndEmpresaId(Long id, Long empresaId);

    List<Compra> findByEmpresaIdAndFechaBetween(Long empresaId, LocalDateTime desde, LocalDateTime hasta);

    List<Compra> findByAlmacenId(Long almacenId);
}
