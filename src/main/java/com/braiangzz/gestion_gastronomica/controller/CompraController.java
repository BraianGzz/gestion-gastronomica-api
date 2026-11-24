package com.braiangzz.gestion_gastronomica.controller;

import com.braiangzz.gestion_gastronomica.dto.request.CompraRequestDto;
import com.braiangzz.gestion_gastronomica.dto.response.CompraResponseDto;
import com.braiangzz.gestion_gastronomica.service.CompraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Controlador REST para la gestión de compras de insumos.
 */
@Slf4j
@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
@Tag(name = "Compras", description = "Registro e historial de compras de insumos")
public class CompraController {

    private final CompraService compraService;

    @PostMapping
    @Operation(summary = "Registrar una compra", description = "Registra una compra de insumo y suma automáticamente la cantidad al stock")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Compra registrada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Insumo no encontrado")
    })
    public ResponseEntity<CompraResponseDto> registrar(@Valid @RequestBody CompraRequestDto request) {
        log.info("POST /api/compras - insumoId={}, cantidad={}", request.getInsumoId(), request.getCantidad());
        return ResponseEntity.status(HttpStatus.CREATED).body(compraService.registrar(request));
    }

    @GetMapping
    @Operation(summary = "Listar compras", description = "Lista compras paginadas, con filtros opcionales por insumo y rango de fechas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "400", description = "Rango de fechas inválido")
    })
    public ResponseEntity<Page<CompraResponseDto>> listar(
            @RequestParam(required = false) Long insumoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @ParameterObject Pageable pageable) {
        log.info("GET /api/compras - insumoId={}, desde={}, hasta={}", insumoId, desde, hasta);
        return ResponseEntity.ok(compraService.listar(insumoId, desde, hasta, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener compra por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compra encontrada"),
            @ApiResponse(responseCode = "404", description = "Compra no encontrada")
    })
    public ResponseEntity<CompraResponseDto> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/compras/{}", id);
        return ResponseEntity.ok(compraService.obtenerPorId(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una compra", description = "Elimina el registro de compra y revierte la cantidad sumada del stock del insumo")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Compra eliminada"),
            @ApiResponse(responseCode = "404", description = "Compra no encontrada"),
            @ApiResponse(responseCode = "409", description = "Stock insuficiente para revertir la compra")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/compras/{}", id);
        compraService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
