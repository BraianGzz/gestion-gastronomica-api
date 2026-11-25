package com.braiangzz.gestion_gastronomica.repository;

import com.braiangzz.gestion_gastronomica.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la gestión de productos y sus ingredientes.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    @EntityGraph(attributePaths = {"ingredientes", "ingredientes.insumo"})
    Optional<Producto> findByIdAndActivoTrue(Long id);

    Page<Producto> findByActivoTrue(Pageable pageable);

    Page<Producto> findByActivoTrueAndNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
