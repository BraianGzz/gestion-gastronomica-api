package com.braiangzz.gestion_gastronomica.dto.response;

import com.braiangzz.gestion_gastronomica.model.UnidadMedida;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO de salida con la información de un insumo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InsumoResponseDto {

    private Long id;
    private String nombre;
    private Integer cantidadStock;
    private UnidadMedida unidad;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
