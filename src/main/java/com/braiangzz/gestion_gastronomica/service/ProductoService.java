package com.braiangzz.gestion_gastronomica.service;

import com.braiangzz.gestion_gastronomica.dto.request.ProductoRequestDto;
import com.braiangzz.gestion_gastronomica.dto.response.ProductoResponseDto;
import com.braiangzz.gestion_gastronomica.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio para la gestión del catálogo de productos y sus recetas.
 */
public interface ProductoService {

    ProductoResponseDto crear(ProductoRequestDto request);

    Page<ProductoResponseDto> listar(String nombre, Pageable pageable);

    ProductoResponseDto obtenerPorId(Long id);

    Producto obtenerEntidadActivaPorId(Long id);

    ProductoResponseDto actualizar(Long id, ProductoRequestDto request);

    void eliminar(Long id);
}
