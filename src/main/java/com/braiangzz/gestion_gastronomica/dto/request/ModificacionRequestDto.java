package com.braiangzz.gestion_gastronomica.dto.request;

import com.braiangzz.gestion_gastronomica.model.TipoModificacion;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO de entrada para agregar o quitar un insumo en una línea de venta.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModificacionRequestDto {

    @NotNull(message = "El id del insumo es obligatorio")
    private Long insumoId;

    @NotNull(message = "El tipo de modificación es obligatorio")
    private TipoModificacion tipoModificacion;

    @Positive(message = "La cantidad extra debe ser mayor a cero")
    private Integer cantidadExtra;

    @PositiveOrZero(message = "El precio extra no puede ser negativo")
    private BigDecimal precioExtra;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String descripcion;
}
