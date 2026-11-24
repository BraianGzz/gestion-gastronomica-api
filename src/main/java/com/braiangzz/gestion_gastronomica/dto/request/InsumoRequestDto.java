package com.braiangzz.gestion_gastronomica.dto.request;

import com.braiangzz.gestion_gastronomica.model.UnidadMedida;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de entrada para crear o actualizar un insumo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InsumoRequestDto {

    @NotBlank(message = "El nombre del insumo es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotNull(message = "La cantidad de stock es obligatoria")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer cantidadStock;

    @NotNull(message = "La unidad de medida es obligatoria")
    private UnidadMedida unidad;
}
