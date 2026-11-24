package com.braiangzz.gestion_gastronomica.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de entrada para ajustar manualmente el stock de un insumo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AjusteStockRequestDto {

    @NotNull(message = "La nueva cantidad de stock es obligatoria")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer cantidadStock;

    @Size(max = 255, message = "El motivo no puede superar los 255 caracteres")
    private String motivo;
}
