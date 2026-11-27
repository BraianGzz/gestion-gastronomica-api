package com.braiangzz.gestion_gastronomica.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entidad que representa el agregado o quita de un insumo en una línea de venta.
 */
@Entity
@Table(
        name = "modificaciones",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_modificacion_linea_insumo",
                columnNames = {"linea_de_venta_id", "insumo_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Modificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "linea_de_venta_id", nullable = false)
    private LineaDeVenta lineaDeVenta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "insumo_id", nullable = false)
    private Insumo insumo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_modificacion", nullable = false, length = 20)
    private TipoModificacion tipoModificacion;

    @Column(name = "cantidad_extra", nullable = false)
    private Integer cantidadExtra;

    @Column(name = "precio_extra", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioExtra = BigDecimal.ZERO;

    @Column(length = 255)
    private String descripcion;
}
