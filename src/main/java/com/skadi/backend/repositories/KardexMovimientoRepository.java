package com.skadi.backend.repositories;

import com.skadi.backend.entities.KardexMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface KardexMovimientoRepository extends JpaRepository<KardexMovimiento, Long> {

    List<KardexMovimiento> findByEmpresaId(Long empresaId);

    List<KardexMovimiento> findByVarianteIdAndAlmacenIdOrderByFechaAsc(Long varianteId, Long almacenId);

    List<KardexMovimiento> findByVarianteIdOrderByFechaAsc(Long varianteId);

    @Query("SELECT k FROM KardexMovimiento k WHERE k.empresa.id = :empresaId " +
            "AND k.variante.id = :varianteId AND k.almacen.id = :almacenId " +
            "ORDER BY k.fecha ASC")
    List<KardexMovimiento> findKardex(@Param("empresaId") Long empresaId,
            @Param("varianteId") Long varianteId,
            @Param("almacenId") Long almacenId);

    @Query("SELECT k FROM KardexMovimiento k WHERE k.empresa.id = :empresaId " +
            "AND k.variante.id = :varianteId AND k.almacen.id = :almacenId " +
            "AND k.lote.id = :loteId ORDER BY k.fecha ASC")
    List<KardexMovimiento> findKardexByLote(@Param("empresaId") Long empresaId,
            @Param("varianteId") Long varianteId,
            @Param("almacenId") Long almacenId,
            @Param("loteId") Long loteId);

    @Query("SELECT k FROM KardexMovimiento k WHERE k.empresa.id = :empresaId " +
            "AND k.fecha BETWEEN :desde AND :hasta ORDER BY k.fecha ASC")
    List<KardexMovimiento> findByEmpresaIdAndFechaBetween(@Param("empresaId") Long empresaId,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta);

    @Query("SELECT k FROM KardexMovimiento k WHERE k.variante.id = :varianteId " +
            "AND k.almacen.id = :almacenId ORDER BY k.fecha DESC LIMIT 1")
    Optional<KardexMovimiento> findLastMovimiento(@Param("varianteId") Long varianteId,
            @Param("almacenId") Long almacenId);

    @Query("SELECT k FROM KardexMovimiento k WHERE k.variante.id = :varianteId " +
            "AND k.almacen.id = :almacenId AND k.lote.id = :loteId ORDER BY k.fecha DESC LIMIT 1")
    Optional<KardexMovimiento> findLastMovimientoByLote(@Param("varianteId") Long varianteId,
            @Param("almacenId") Long almacenId,
            @Param("loteId") Long loteId);
}
