package com.braiangzz.gestion_gastronomica.service;

import com.braiangzz.gestion_gastronomica.dto.request.AjusteStockRequestDto;
import com.braiangzz.gestion_gastronomica.dto.request.InsumoRequestDto;
import com.braiangzz.gestion_gastronomica.dto.response.InsumoResponseDto;
import com.braiangzz.gestion_gastronomica.model.Insumo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio para la gestión de insumos e inventario.
 */
public interface InsumoService {

    InsumoResponseDto crear(InsumoRequestDto request);

    Page<InsumoResponseDto> listar(String nombre, Pageable pageable);

    InsumoResponseDto obtenerPorId(Long id);

    InsumoResponseDto actualizar(Long id, InsumoRequestDto request);

    InsumoResponseDto ajustarStock(Long id, AjusteStockRequestDto request);

    Insumo sumarStock(Long id, Integer cantidad);

    Insumo descontarStock(Long id, Integer cantidad);

    void eliminar(Long id);
}
