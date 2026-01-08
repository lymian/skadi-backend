package com.skadi.backend.repositories;

import com.skadi.backend.entities.AjusteInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AjusteInventarioRepository extends JpaRepository<AjusteInventario, Long> {

    List<AjusteInventario> findByEmpresaId(Long empresaId);

    List<AjusteInventario> findByAlmacenId(Long almacenId);

    List<AjusteInventario> findByVarianteId(Long varianteId);
}
