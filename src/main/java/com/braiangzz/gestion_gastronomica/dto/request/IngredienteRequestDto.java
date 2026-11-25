package com.braiangzz.gestion_gastronomica.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de entrada para definir un ingrediente dentro de la receta de un producto.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngredienteRequestDto {

    @NotNull(message = "El id del insumo es obligatorio")
    private Long insumoId;

    @NotNull(message = "La cantidad consumida es obligatoria")
    @Positive(message = "La cantidad consumida debe ser mayor a cero")
    private Integer cantidadConsumida;
}
