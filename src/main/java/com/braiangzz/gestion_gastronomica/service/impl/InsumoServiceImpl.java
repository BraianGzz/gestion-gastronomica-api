package com.braiangzz.gestion_gastronomica.service.impl;

import com.braiangzz.gestion_gastronomica.dto.request.AjusteStockRequestDto;
import com.braiangzz.gestion_gastronomica.dto.request.InsumoRequestDto;
import com.braiangzz.gestion_gastronomica.dto.response.InsumoResponseDto;
import com.braiangzz.gestion_gastronomica.exception.BusinessException;
import com.braiangzz.gestion_gastronomica.exception.InvalidRequestException;
import com.braiangzz.gestion_gastronomica.exception.ResourceNotFoundException;
import com.braiangzz.gestion_gastronomica.mapper.InsumoMapper;
import com.braiangzz.gestion_gastronomica.model.Insumo;
import com.braiangzz.gestion_gastronomica.repository.InsumoRepository;
import com.braiangzz.gestion_gastronomica.service.InsumoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de gestión de insumos.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InsumoServiceImpl implements InsumoService {

    private final InsumoRepository insumoRepository;
    private final InsumoMapper insumoMapper;

    @Override
    @Transactional
    public InsumoResponseDto crear(InsumoRequestDto request) {
        String nombreNormalizado = request.getNombre().trim();
        if (insumoRepository.existsByNombreIgnoreCase(nombreNormalizado)) {
            throw new BusinessException("Ya existe un insumo registrado con el nombre: " + nombreNormalizado);
        }

        Insumo insumo = insumoMapper.toEntity(request);
        insumo.setNombre(nombreNormalizado);
        insumo.setActivo(true);

        Insumo guardado = insumoRepository.save(insumo);
        log.info("Insumo creado con id={} y nombre={}", guardado.getId(), guardado.getNombre());
        return insumoMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InsumoResponseDto> listar(String nombre, Pageable pageable) {
        Page<Insumo> insumos;
        if (nombre != null && !nombre.isBlank()) {
            insumos = insumoRepository.findByActivoTrueAndNombreContainingIgnoreCase(nombre.trim(), pageable);
        } else {
            insumos = insumoRepository.findByActivoTrue(pageable);
        }
        return insumos.map(insumoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public InsumoResponseDto obtenerPorId(Long id) {
        Insumo insumo = buscarActivoPorId(id);
        return insumoMapper.toResponse(insumo);
    }

    @Override
    @Transactional(readOnly = true)
    public Insumo obtenerEntidadActivaPorId(Long id) {
        return buscarActivoPorId(id);
    }

    @Override
    @Transactional
    public InsumoResponseDto actualizar(Long id, InsumoRequestDto request) {
        Insumo insumo = buscarActivoPorId(id);
        String nombreNormalizado = request.getNombre().trim();

        if (insumoRepository.existsByNombreIgnoreCaseAndIdNot(nombreNormalizado, id)) {
            throw new BusinessException("Ya existe otro insumo registrado con el nombre: " + nombreNormalizado);
        }

        insumoMapper.updateEntity(request, insumo);
        insumo.setNombre(nombreNormalizado);

        Insumo actualizado = insumoRepository.save(insumo);
        log.info("Insumo actualizado con id={}", actualizado.getId());
        return insumoMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public InsumoResponseDto ajustarStock(Long id, AjusteStockRequestDto request) {
        Insumo insumo = buscarActivoPorId(id);
        Integer stockAnterior = insumo.getCantidadStock();
        insumo.setCantidadStock(request.getCantidadStock());

        Insumo actualizado = insumoRepository.save(insumo);
        log.info("Stock ajustado para insumo id={}: {} -> {} (motivo: {})",
                id, stockAnterior, actualizado.getCantidadStock(), request.getMotivo());
        return insumoMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public Insumo sumarStock(Long id, Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new InvalidRequestException("La cantidad a sumar al stock debe ser mayor a cero");
        }
        Insumo insumo = buscarActivoPorId(id);
        Integer stockAnterior = insumo.getCantidadStock();
        insumo.setCantidadStock(stockAnterior + cantidad);

        Insumo actualizado = insumoRepository.save(insumo);
        log.info("Stock incrementado para insumo id={}: {} -> {} (+{})",
                id, stockAnterior, actualizado.getCantidadStock(), cantidad);
        return actualizado;
    }

    @Override
    @Transactional
    public Insumo descontarStock(Long id, Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new InvalidRequestException("La cantidad a descontar del stock debe ser mayor a cero");
        }
        Insumo insumo = buscarActivoPorId(id);
        Integer stockAnterior = insumo.getCantidadStock();
        if (stockAnterior < cantidad) {
            throw new BusinessException("Stock insuficiente para el insumo '" + insumo.getNombre()
                    + "'. Disponible: " + stockAnterior + ", requerido: " + cantidad);
        }
        insumo.setCantidadStock(stockAnterior - cantidad);

        Insumo actualizado = insumoRepository.save(insumo);
        log.info("Stock descontado para insumo id={}: {} -> {} (-{})",
                id, stockAnterior, actualizado.getCantidadStock(), cantidad);
        return actualizado;
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Insumo insumo = buscarActivoPorId(id);
        insumo.setActivo(false);
        insumoRepository.save(insumo);
        log.info("Insumo dado de baja lógica con id={}", id);
    }

    private Insumo buscarActivoPorId(Long id) {
        return insumoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado con id: " + id));
    }
}
