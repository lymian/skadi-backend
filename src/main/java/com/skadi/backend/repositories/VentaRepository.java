package com.skadi.backend.repositories;

import com.skadi.backend.entities.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByEmpresaId(Long empresaId);

    Optional<Venta> findByIdAndEmpresaId(Long id, Long empresaId);

    List<Venta> findByEmpresaIdAndFechaBetween(Long empresaId, LocalDateTime desde, LocalDateTime hasta);

    List<Venta> findByAlmacenId(Long almacenId);
}
