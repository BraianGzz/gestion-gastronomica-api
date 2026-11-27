package com.braiangzz.gestion_gastronomica.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de salida con el detalle de una línea de venta y sus modificaciones.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LineaDeVentaResponseDto {

    private Long id;
    private Long productoId;
    private String productoNombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private List<ModificacionResponseDto> modificaciones;
}
