package com.skadi.backend.repositories;

import com.skadi.backend.entities.ProductoVariante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoVarianteRepository extends JpaRepository<ProductoVariante, Long> {

    List<ProductoVariante> findByProductoId(Long productoId);

    Optional<ProductoVariante> findByIdAndProductoId(Long id, Long productoId);

    Optional<ProductoVariante> findBySku(String sku);

    boolean existsBySku(String sku);
}
