package com.braiangzz.gestion_gastronomica.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO de entrada que representa un producto y sus modificaciones dentro de una venta.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LineaDeVentaRequestDto {

    @NotNull(message = "El id del producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;

    @Valid
    private List<ModificacionRequestDto> modificaciones;
}
