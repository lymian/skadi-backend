package com.skadi.backend.repositories;

import com.skadi.backend.entities.StockAlmacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockAlmacenRepository extends JpaRepository<StockAlmacen, Long> {

        List<StockAlmacen> findByEmpresaId(Long empresaId);

        List<StockAlmacen> findByAlmacenId(Long almacenId);

        List<StockAlmacen> findByVarianteId(Long varianteId);

        Optional<StockAlmacen> findByAlmacenIdAndVarianteIdAndLoteId(Long almacenId, Long varianteId, Long loteId);

        Optional<StockAlmacen> findByAlmacenIdAndVarianteIdAndLoteIsNull(Long almacenId, Long varianteId);

        @Query("SELECT s FROM StockAlmacen s WHERE s.empresa.id = :empresaId AND s.almacen.id = :almacenId")
        List<StockAlmacen> findByEmpresaIdAndAlmacenId(@Param("empresaId") Long empresaId,
                        @Param("almacenId") Long almacenId);

        @Query("SELECT s FROM StockAlmacen s WHERE s.empresa.id = :empresaId AND s.variante.id = :varianteId")
        List<StockAlmacen> findByEmpresaIdAndVarianteId(@Param("empresaId") Long empresaId,
                        @Param("varianteId") Long varianteId);

        @Query("SELECT s FROM StockAlmacen s WHERE s.lote.id = :loteId ORDER BY s.lote.fechaVencimiento ASC")
        List<StockAlmacen> findByLoteIdOrderByFechaVencimiento(@Param("loteId") Long loteId);

        @Query("SELECT s FROM StockAlmacen s LEFT JOIN s.lote l WHERE s.almacen.id = :almacenId AND s.variante.id = :varianteId AND s.stock > 0 ORDER BY CASE WHEN l.fechaVencimiento IS NULL THEN 1 ELSE 0 END, l.fechaVencimiento ASC")
        List<StockAlmacen> findAvailableStockFIFO(@Param("almacenId") Long almacenId,
                        @Param("varianteId") Long varianteId);

        // FIFO solo con lotes (excluye stock sin lote) - para productos que requieren
        // lote
        @Query("SELECT s FROM StockAlmacen s JOIN s.lote l WHERE s.almacen.id = :almacenId AND s.variante.id = :varianteId AND s.stock > 0 ORDER BY l.fechaVencimiento ASC NULLS LAST")
        List<StockAlmacen> findAvailableStockWithLoteFIFO(@Param("almacenId") Long almacenId,
                        @Param("varianteId") Long varianteId);
}
