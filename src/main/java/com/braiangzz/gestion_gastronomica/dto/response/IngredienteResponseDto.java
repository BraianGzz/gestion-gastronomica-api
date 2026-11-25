package com.braiangzz.gestion_gastronomica.dto.response;

import com.braiangzz.gestion_gastronomica.model.UnidadMedida;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de salida con el detalle de un ingrediente de la receta.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngredienteResponseDto {

    private Long id;
    private Long insumoId;
    private String insumoNombre;
    private UnidadMedida insumoUnidad;
    private Integer cantidadConsumida;
}
