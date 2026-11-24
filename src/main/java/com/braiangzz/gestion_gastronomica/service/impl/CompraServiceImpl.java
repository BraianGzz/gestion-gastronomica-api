package com.braiangzz.gestion_gastronomica.service.impl;

import com.braiangzz.gestion_gastronomica.dto.request.CompraRequestDto;
import com.braiangzz.gestion_gastronomica.dto.response.CompraResponseDto;
import com.braiangzz.gestion_gastronomica.exception.InvalidRequestException;
import com.braiangzz.gestion_gastronomica.exception.ResourceNotFoundException;
import com.braiangzz.gestion_gastronomica.mapper.CompraMapper;
import com.braiangzz.gestion_gastronomica.model.Compra;
import com.braiangzz.gestion_gastronomica.model.Insumo;
import com.braiangzz.gestion_gastronomica.repository.CompraRepository;
import com.braiangzz.gestion_gastronomica.service.CompraService;
import com.braiangzz.gestion_gastronomica.service.InsumoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Implementación del servicio de gestión de compras de insumos.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CompraServiceImpl implements CompraService {

    private final CompraRepository compraRepository;
    private final InsumoService insumoService;
    private final CompraMapper compraMapper;

    @Override
    @Transactional
    public CompraResponseDto registrar(CompraRequestDto request) {
        Insumo insumoActualizado = insumoService.sumarStock(request.getInsumoId(), request.getCantidad());

        Compra compra = compraMapper.toEntity(request);
        compra.setInsumo(insumoActualizado);

        Compra guardada = compraRepository.save(compra);
        log.info("Compra registrada con id={}, insumoId={}, cantidad={}, costo={}",
                guardada.getId(), insumoActualizado.getId(), guardada.getCantidad(), guardada.getCosto());
        return compraMapper.toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompraResponseDto> listar(Long insumoId, LocalDate desde, LocalDate hasta, Pageable pageable) {
        if ((desde != null && hasta == null) || (desde == null && hasta != null)) {
            throw new InvalidRequestException("Debe indicar tanto la fecha 'desde' como la fecha 'hasta'");
        }
        if (desde != null && desde.isAfter(hasta)) {
            throw new InvalidRequestException("La fecha 'desde' no puede ser posterior a la fecha 'hasta'");
        }

        Page<Compra> compras;
        if (insumoId != null && desde != null) {
            compras = compraRepository.findByInsumoIdAndFechaBetween(insumoId, desde, hasta, pageable);
        } else if (insumoId != null) {
            compras = compraRepository.findByInsumoId(insumoId, pageable);
        } else if (desde != null) {
            compras = compraRepository.findByFechaBetween(desde, hasta, pageable);
        } else {
            compras = compraRepository.findAll(pageable);
        }

        return compras.map(compraMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CompraResponseDto obtenerPorId(Long id) {
        Compra compra = buscarPorId(id);
        return compraMapper.toResponse(compra);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Compra compra = buscarPorId(id);
        insumoService.descontarStock(compra.getInsumo().getId(), compra.getCantidad());
        compraRepository.delete(compra);
        log.info("Compra eliminada con id={} y stock revertido para insumoId={}",
                id, compra.getInsumo().getId());
    }

    private Compra buscarPorId(Long id) {
        return compraRepository.findWithInsumoById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con id: " + id));
    }
}
