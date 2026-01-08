package com.skadi.backend.repositories;

import com.skadi.backend.entities.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {

    List<Lote> findByVarianteId(Long varianteId);

    Optional<Lote> findByIdAndVarianteId(Long id, Long varianteId);

    Optional<Lote> findByCodigoLoteAndVarianteId(String codigoLote, Long varianteId);

    @Query("SELECT l FROM Lote l JOIN l.variante v JOIN v.producto p " +
            "WHERE p.empresa.id = :empresaId AND l.fechaVencimiento <= :fecha")
    List<Lote> findLotesPorVencer(@Param("empresaId") Long empresaId, @Param("fecha") LocalDate fecha);
}
