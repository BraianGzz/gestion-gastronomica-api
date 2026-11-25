package com.braiangzz.gestion_gastronomica.service.impl;

import com.braiangzz.gestion_gastronomica.dto.request.IngredienteRequestDto;
import com.braiangzz.gestion_gastronomica.dto.request.ProductoRequestDto;
import com.braiangzz.gestion_gastronomica.dto.response.ProductoResponseDto;
import com.braiangzz.gestion_gastronomica.exception.BusinessException;
import com.braiangzz.gestion_gastronomica.exception.InvalidRequestException;
import com.braiangzz.gestion_gastronomica.exception.ResourceNotFoundException;
import com.braiangzz.gestion_gastronomica.mapper.ProductoMapper;
import com.braiangzz.gestion_gastronomica.model.Ingrediente;
import com.braiangzz.gestion_gastronomica.model.Insumo;
import com.braiangzz.gestion_gastronomica.model.Producto;
import com.braiangzz.gestion_gastronomica.repository.ProductoRepository;
import com.braiangzz.gestion_gastronomica.service.InsumoService;
import com.braiangzz.gestion_gastronomica.service.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementación del servicio de gestión de productos y sus recetas.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final InsumoService insumoService;
    private final ProductoMapper productoMapper;

    @Override
    @Transactional
    public ProductoResponseDto crear(ProductoRequestDto request) {
        String nombreNormalizado = request.getNombre().trim();
        if (productoRepository.existsByNombreIgnoreCase(nombreNormalizado)) {
            throw new BusinessException("Ya existe un producto registrado con el nombre: " + nombreNormalizado);
        }

        validarSinInsumosRepetidos(request.getIngredientes());

        Producto producto = productoMapper.toEntity(request);
        producto.setNombre(nombreNormalizado);
        producto.setActivo(true);
        if (producto.getIngredientes() == null) {
            producto.setIngredientes(new ArrayList<>());
        }

        for (IngredienteRequestDto item : request.getIngredientes()) {
            Insumo insumo = insumoService.obtenerEntidadActivaPorId(item.getInsumoId());
            producto.agregarIngrediente(new Ingrediente(null, producto, insumo, item.getCantidadConsumida()));
        }

        Producto guardado = productoRepository.save(producto);
        log.info("Producto creado con id={}, nombre={}, ingredientes={}",
                guardado.getId(), guardado.getNombre(), guardado.getIngredientes().size());
        return productoMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponseDto> listar(String nombre, Pageable pageable) {
        Page<Producto> productos;
        if (nombre != null && !nombre.isBlank()) {
            productos = productoRepository.findByActivoTrueAndNombreContainingIgnoreCase(nombre.trim(), pageable);
        } else {
            productos = productoRepository.findByActivoTrue(pageable);
        }
        return productos.map(productoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDto obtenerPorId(Long id) {
        Producto producto = buscarActivoPorId(id);
        return productoMapper.toResponse(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Producto obtenerEntidadActivaPorId(Long id) {
        return buscarActivoPorId(id);
    }

    @Override
    @Transactional
    public ProductoResponseDto actualizar(Long id, ProductoRequestDto request) {
        Producto producto = buscarActivoPorId(id);
        String nombreNormalizado = request.getNombre().trim();

        if (productoRepository.existsByNombreIgnoreCaseAndIdNot(nombreNormalizado, id)) {
            throw new BusinessException("Ya existe otro producto registrado con el nombre: " + nombreNormalizado);
        }

        validarSinInsumosRepetidos(request.getIngredientes());

        productoMapper.updateEntity(request, producto);
        producto.setNombre(nombreNormalizado);

        sincronizarIngredientes(producto, request.getIngredientes());

        Producto actualizado = productoRepository.save(producto);
        log.info("Producto actualizado con id={}, ingredientes={}",
                actualizado.getId(), actualizado.getIngredientes().size());
        return productoMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Producto producto = buscarActivoPorId(id);
        producto.setActivo(false);
        productoRepository.save(producto);
        log.info("Producto dado de baja lógica con id={}", id);
    }

    private Producto buscarActivoPorId(Long id) {
        return productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }

    private void validarSinInsumosRepetidos(List<IngredienteRequestDto> ingredientes) {
        Set<Long> idsVistos = new HashSet<>();
        for (IngredienteRequestDto item : ingredientes) {
            if (item.getInsumoId() == null || !idsVistos.add(item.getInsumoId())) {
                throw new InvalidRequestException(
                        "No se puede repetir el mismo insumo en la receta (insumoId: " + item.getInsumoId() + ")");
            }
        }
    }

    /**
     * Sincroniza la lista administrada in-place para evitar conflictos de orden
     * INSERT/DELETE en Hibernate sobre la restricción UNIQUE (producto_id, insumo_id).
     */
    private void sincronizarIngredientes(Producto producto, List<IngredienteRequestDto> nuevosIngredientes) {
        Map<Long, Integer> nuevosPorInsumoId = new LinkedHashMap<>();
        for (IngredienteRequestDto item : nuevosIngredientes) {
            nuevosPorInsumoId.put(item.getInsumoId(), item.getCantidadConsumida());
        }

        producto.getIngredientes().removeIf(ing -> !nuevosPorInsumoId.containsKey(ing.getInsumo().getId()));

        Map<Long, Ingrediente> existentesPorInsumoId = new LinkedHashMap<>();
        for (Ingrediente ing : producto.getIngredientes()) {
            existentesPorInsumoId.put(ing.getInsumo().getId(), ing);
        }

        for (IngredienteRequestDto item : nuevosIngredientes) {
            Ingrediente existente = existentesPorInsumoId.get(item.getInsumoId());
            if (existente != null) {
                existente.setCantidadConsumida(item.getCantidadConsumida());
            } else {
                Insumo insumo = insumoService.obtenerEntidadActivaPorId(item.getInsumoId());
                producto.agregarIngrediente(new Ingrediente(null, producto, insumo, item.getCantidadConsumida()));
            }
        }
    }
}
