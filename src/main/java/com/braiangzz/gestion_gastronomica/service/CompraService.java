package com.braiangzz.gestion_gastronomica.service;

import com.braiangzz.gestion_gastronomica.dto.request.CompraRequestDto;
import com.braiangzz.gestion_gastronomica.dto.response.CompraResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

/**
 * Servicio para la gestión de compras de insumos.
 */
public interface CompraService {

    CompraResponseDto registrar(CompraRequestDto request);

    Page<CompraResponseDto> listar(Long insumoId, LocalDate desde, LocalDate hasta, Pageable pageable);

    CompraResponseDto obtenerPorId(Long id);

    void eliminar(Long id);
}
