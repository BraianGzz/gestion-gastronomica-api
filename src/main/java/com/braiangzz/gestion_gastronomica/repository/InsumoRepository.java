package com.braiangzz.gestion_gastronomica.repository;

import com.braiangzz.gestion_gastronomica.model.Insumo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la gestión de insumos.
 */
@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    Optional<Insumo> findByIdAndActivoTrue(Long id);

    Page<Insumo> findByActivoTrue(Pageable pageable);

    Page<Insumo> findByActivoTrueAndNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
