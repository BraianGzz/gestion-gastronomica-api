package com.braiangzz.gestion_gastronomica.dto.response;

import com.braiangzz.gestion_gastronomica.model.TipoModificacion;
import com.braiangzz.gestion_gastronomica.model.UnidadMedida;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO de salida con el detalle de una modificación aplicada en una línea de venta.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModificacionResponseDto {

    private Long id;
    private Long insumoId;
    private String insumoNombre;
    private UnidadMedida insumoUnidad;
    private TipoModificacion tipoModificacion;
    private Integer cantidadExtra;
    private BigDecimal precioExtra;
    private String descripcion;
}
