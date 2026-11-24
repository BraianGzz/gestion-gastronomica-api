package com.braiangzz.gestion_gastronomica.controller;

import com.braiangzz.gestion_gastronomica.dto.request.AjusteStockRequestDto;
import com.braiangzz.gestion_gastronomica.dto.request.InsumoRequestDto;
import com.braiangzz.gestion_gastronomica.dto.response.InsumoResponseDto;
import com.braiangzz.gestion_gastronomica.service.InsumoService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la gestión de insumos.
 */
@Slf4j
@RestController
@RequestMapping("/api/insumos")
@RequiredArgsConstructor
@Tag(name = "Insumos", description = "Gestión de insumos e inventario")
public class InsumoController {

    private final InsumoService insumoService;

    @PostMapping
    @Operation(summary = "Crear un insumo", description = "Registra un nuevo insumo en el inventario")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Insumo creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe un insumo con ese nombre")
    })
    public ResponseEntity<InsumoResponseDto> crear(@Valid @RequestBody InsumoRequestDto request) {
        log.info("POST /api/insumos - nombre={}", request.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(insumoService.crear(request));
    }

    @GetMapping
    @Operation(summary = "Listar insumos", description = "Lista insumos activos paginados, con filtro opcional por nombre")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    public ResponseEntity<Page<InsumoResponseDto>> listar(
            @RequestParam(required = false) String nombre,
            @ParameterObject Pageable pageable) {
        log.info("GET /api/insumos - nombre={}", nombre);
        return ResponseEntity.ok(insumoService.listar(nombre, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener insumo por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Insumo encontrado"),
            @ApiResponse(responseCode = "404", description = "Insumo no encontrado")
    })
    public ResponseEntity<InsumoResponseDto> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/insumos/{}", id);
        return ResponseEntity.ok(insumoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un insumo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Insumo actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Insumo no encontrado"),
            @ApiResponse(responseCode = "409", description = "Ya existe otro insumo con ese nombre")
    })
    public ResponseEntity<InsumoResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody InsumoRequestDto request) {
        log.info("PUT /api/insumos/{}", id);
        return ResponseEntity.ok(insumoService.actualizar(id, request));
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Ajustar stock manualmente", description = "Reemplaza la cantidad de stock actual del insumo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock actualizado"),
            @ApiResponse(responseCode = "400", description = "Stock inválido"),
            @ApiResponse(responseCode = "404", description = "Insumo no encontrado")
    })
    public ResponseEntity<InsumoResponseDto> ajustarStock(
            @PathVariable Long id,
            @Valid @RequestBody AjusteStockRequestDto request) {
        log.info("PATCH /api/insumos/{}/stock", id);
        return ResponseEntity.ok(insumoService.ajustarStock(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un insumo", description = "Realiza una baja lógica del insumo")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Insumo eliminado"),
            @ApiResponse(responseCode = "404", description = "Insumo no encontrado")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/insumos/{}", id);
        insumoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
