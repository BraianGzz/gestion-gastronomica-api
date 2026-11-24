package com.braiangzz.gestion_gastronomica.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de entrada para registrar una compra de insumo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompraRequestDto {

    @NotNull(message = "El id del insumo es obligatorio")
    private Long insumoId;

    @NotNull(message = "La fecha de compra es obligatoria")
    @PastOrPresent(message = "La fecha de compra no puede ser futura")
    private LocalDate fecha;

    @NotNull(message = "La cantidad comprada es obligatoria")
    @Positive(message = "La cantidad comprada debe ser mayor a cero")
    private Integer cantidad;

    @NotNull(message = "El costo de la compra es obligatorio")
    @DecimalMin(value = "0.00", message = "El costo no puede ser negativo")
    private BigDecimal costo;
}
