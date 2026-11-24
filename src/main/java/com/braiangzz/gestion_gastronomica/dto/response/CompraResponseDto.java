package com.braiangzz.gestion_gastronomica.dto.response;

import com.braiangzz.gestion_gastronomica.model.UnidadMedida;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de salida con la información de una compra registrada.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompraResponseDto {

    private Long id;
    private Long insumoId;
    private String insumoNombre;
    private UnidadMedida insumoUnidad;
    private LocalDate fecha;
    private Integer cantidad;
    private BigDecimal costo;
    private LocalDateTime createdAt;
}
