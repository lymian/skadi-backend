package com.skadi.backend.repositories;

import com.skadi.backend.entities.VentaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaDetalleRepository extends JpaRepository<VentaDetalle, Long> {

    List<VentaDetalle> findByVentaId(Long ventaId);
}
