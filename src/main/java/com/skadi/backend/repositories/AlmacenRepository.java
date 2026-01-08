package com.skadi.backend.repositories;

import com.skadi.backend.entities.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlmacenRepository extends JpaRepository<Almacen, Long> {

    List<Almacen> findByEmpresaId(Long empresaId);

    Optional<Almacen> findByIdAndEmpresaId(Long id, Long empresaId);

    List<Almacen> findByEmpresaIdAndEstado(Long empresaId, String estado);
}
