package com.braiangzz.gestion_gastronomica.repository;

import com.braiangzz.gestion_gastronomica.model.Compra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repositorio JPA para la gestión de compras de insumos.
 */
@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    @Override
    @EntityGraph(attributePaths = "insumo")
    Page<Compra> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "insumo")
    Optional<Compra> findWithInsumoById(Long id);

    @EntityGraph(attributePaths = "insumo")
    Page<Compra> findByInsumoId(Long insumoId, Pageable pageable);

    @EntityGraph(attributePaths = "insumo")
    Page<Compra> findByFechaBetween(LocalDate desde, LocalDate hasta, Pageable pageable);

    @EntityGraph(attributePaths = "insumo")
    Page<Compra> findByInsumoIdAndFechaBetween(Long insumoId, LocalDate desde, LocalDate hasta, Pageable pageable);
}
