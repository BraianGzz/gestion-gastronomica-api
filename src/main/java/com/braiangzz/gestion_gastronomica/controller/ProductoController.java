package com.braiangzz.gestion_gastronomica.controller;

import com.braiangzz.gestion_gastronomica.dto.request.ProductoRequestDto;
import com.braiangzz.gestion_gastronomica.dto.response.ProductoResponseDto;
import com.braiangzz.gestion_gastronomica.service.ProductoService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la gestión de productos y sus recetas.
 */
@Slf4j
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Gestión del catálogo de productos y sus recetas")
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    @Operation(summary = "Crear un producto", description = "Registra un nuevo producto con su receta de ingredientes")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o insumos repetidos en la receta"),
            @ApiResponse(responseCode = "404", description = "Alguno de los insumos indicados no existe o está inactivo"),
            @ApiResponse(responseCode = "409", description = "Ya existe un producto con ese nombre")
    })
    public ResponseEntity<ProductoResponseDto> crear(@Valid @RequestBody ProductoRequestDto request) {
        log.info("POST /api/productos - nombre={}", request.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(request));
    }

    @GetMapping
    @Operation(summary = "Listar productos", description = "Lista productos activos paginados, con filtro opcional por nombre")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    public ResponseEntity<Page<ProductoResponseDto>> listar(
            @RequestParam(required = false) String nombre,
            @ParameterObject Pageable pageable) {
        log.info("GET /api/productos - nombre={}", nombre);
        return ResponseEntity.ok(productoService.listar(nombre, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductoResponseDto> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/productos/{}", id);
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto", description = "Actualiza los datos del producto y reemplaza su receta de ingredientes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o insumos repetidos en la receta"),
            @ApiResponse(responseCode = "404", description = "Producto o insumo no encontrado"),
            @ApiResponse(responseCode = "409", description = "Ya existe otro producto con ese nombre")
    })
    public ResponseEntity<ProductoResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDto request) {
        log.info("PUT /api/productos/{}", id);
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto", description = "Realiza una baja lógica del producto")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/productos/{}", id);
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
