package com.braiangzz.gestion_gastronomica.dto.response;

import com.braiangzz.gestion_gastronomica.model.EstadoVenta;
import com.braiangzz.gestion_gastronomica.model.FormaPago;
import com.braiangzz.gestion_gastronomica.model.TipoEntrega;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de salida con la información completa de una venta para historial, ticket o comanda.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VentaResponseDto {

    private Long id;
    private String nombreCliente;
    private LocalDate fecha;
    private EstadoVenta estado;
    private TipoEntrega tipoEntrega;
    private String direccion;
    private FormaPago formaPago;
    private BigDecimal descuento;
    private BigDecimal total;
    private List<LineaDeVentaResponseDto> lineas;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
