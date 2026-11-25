package com.braiangzz.gestion_gastronomica.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de salida con la información completa de un producto y su receta.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponseDto {

    private Long id;
    private String nombre;
    private BigDecimal precio;
    private String descripcion;
    private Boolean activo;
    private List<IngredienteResponseDto> ingredientes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
