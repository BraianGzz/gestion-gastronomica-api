package com.braiangzz.gestion_gastronomica.repository;

import com.braiangzz.gestion_gastronomica.model.EstadoVenta;
import com.braiangzz.gestion_gastronomica.model.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repositorio JPA para la gestión de ventas, sus líneas y modificaciones.
 */
@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    @EntityGraph(attributePaths = {"lineas", "lineas.producto"})
    Optional<Venta> findWithLineasById(Long id);

    Page<Venta> findByEstado(EstadoVenta estado, Pageable pageable);

    Page<Venta> findByFechaBetween(LocalDate desde, LocalDate hasta, Pageable pageable);

    Page<Venta> findByEstadoAndFechaBetween(EstadoVenta estado, LocalDate desde, LocalDate hasta, Pageable pageable);
}
