package com.braiangzz.gestion_gastronomica.dto.request;

import com.braiangzz.gestion_gastronomica.model.FormaPago;
import com.braiangzz.gestion_gastronomica.model.TipoEntrega;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO de entrada para registrar una venta con sus líneas y modificaciones.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VentaRequestDto {

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(max = 100, message = "El nombre del cliente no puede superar los 100 caracteres")
    private String nombreCliente;

    private LocalDate fecha;

    @NotNull(message = "El tipo de entrega es obligatorio")
    private TipoEntrega tipoEntrega;

    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres")
    private String direccion;

    @NotNull(message = "La forma de pago es obligatoria")
    private FormaPago formaPago;

    @Min(value = 0, message = "El descuento no puede ser menor a 0")
    @Max(value = 100, message = "El descuento no puede superar el 100%")
    private Integer descuento;

    @NotEmpty(message = "La venta debe tener al menos una línea de venta")
    @Valid
    private List<LineaDeVentaRequestDto> lineas;
}
