package com.braiangzz.gestion_gastronomica.service;

import com.braiangzz.gestion_gastronomica.dto.request.VentaRequestDto;
import com.braiangzz.gestion_gastronomica.dto.response.VentaResponseDto;
import com.braiangzz.gestion_gastronomica.model.EstadoVenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

/**
 * Servicio para el registro, consulta y anulación de ventas.
 */
public interface VentaService {

    VentaResponseDto registrar(VentaRequestDto request);

    Page<VentaResponseDto> listar(EstadoVenta estado, LocalDate desde, LocalDate hasta, Pageable pageable);

    VentaResponseDto obtenerPorId(Long id);

    VentaResponseDto anular(Long id);
}
