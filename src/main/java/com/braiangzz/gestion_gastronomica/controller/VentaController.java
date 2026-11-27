package com.braiangzz.gestion_gastronomica.controller;

import com.braiangzz.gestion_gastronomica.dto.request.VentaRequestDto;
import com.braiangzz.gestion_gastronomica.dto.response.VentaResponseDto;
import com.braiangzz.gestion_gastronomica.model.EstadoVenta;
import com.braiangzz.gestion_gastronomica.service.VentaService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Controlador REST para el registro, consulta y anulación de ventas.
 */
@Slf4j
@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@Tag(name = "Ventas", description = "Registro, consulta y anulación de ventas con descuento automático de stock")
public class VentaController {

    private final VentaService ventaService;

    @PostMapping
    @Operation(summary = "Registrar una venta", description = "Calcula subtotales y total con modificaciones, registra la venta y descuenta el stock de insumos")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Venta registrada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o modificación incorrecta"),
            @ApiResponse(responseCode = "404", description = "Producto o insumo no encontrado o inactivo"),
            @ApiResponse(responseCode = "409", description = "Stock insuficiente para alguno de los insumos requeridos")
    })
    public ResponseEntity<VentaResponseDto> registrar(@Valid @RequestBody VentaRequestDto request) {
        log.info("POST /api/ventas - cliente={}, lineas={}", request.getNombreCliente(), request.getLineas().size());
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.registrar(request));
    }

    @GetMapping
    @Operation(summary = "Listar ventas", description = "Lista ventas paginadas, con filtros opcionales por estado y rango de fechas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "400", description = "Rango de fechas inválido")
    })
    public ResponseEntity<Page<VentaResponseDto>> listar(
            @RequestParam(required = false) EstadoVenta estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @ParameterObject Pageable pageable) {
        log.info("GET /api/ventas - estado={}, desde={}, hasta={}", estado, desde, hasta);
        return ResponseEntity.ok(ventaService.listar(estado, desde, hasta, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener venta por ID", description = "Devuelve el detalle completo de la venta, líneas y modificaciones (apto para consulta, ticket y comanda)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venta encontrada"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
    public ResponseEntity<VentaResponseDto> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/ventas/{}", id);
        return ResponseEntity.ok(ventaService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/anular")
    @Operation(summary = "Anular una venta", description = "Cambia el estado de la venta a ANULADA y restituye al stock los insumos consumidos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venta anulada y stock restituido"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada"),
            @ApiResponse(responseCode = "409", description = "La venta ya se encontraba anulada")
    })
    public ResponseEntity<VentaResponseDto> anular(@PathVariable Long id) {
        log.info("PATCH /api/ventas/{}/anular", id);
        return ResponseEntity.ok(ventaService.anular(id));
    }
}
