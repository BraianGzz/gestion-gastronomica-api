package com.braiangzz.gestion_gastronomica.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un producto y su cantidad dentro de una venta.
 */
@Entity
@Table(name = "lineas_de_venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LineaDeVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @BatchSize(size = 20)
    @OneToMany(mappedBy = "lineaDeVenta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Modificacion> modificaciones = new ArrayList<>();

    /**
     * Asocia una modificación a la línea manteniendo sincronizada la relación bidireccional.
     */
    public void agregarModificacion(Modificacion modificacion) {
        modificacion.setLineaDeVenta(this);
        this.modificaciones.add(modificacion);
    }
}
