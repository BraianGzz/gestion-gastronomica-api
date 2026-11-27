package com.braiangzz.gestion_gastronomica.mapper;

import com.braiangzz.gestion_gastronomica.dto.response.LineaDeVentaResponseDto;
import com.braiangzz.gestion_gastronomica.dto.response.ModificacionResponseDto;
import com.braiangzz.gestion_gastronomica.dto.response.VentaResponseDto;
import com.braiangzz.gestion_gastronomica.mapper.config.MapperConfiguration;
import com.braiangzz.gestion_gastronomica.model.LineaDeVenta;
import com.braiangzz.gestion_gastronomica.model.Modificacion;
import com.braiangzz.gestion_gastronomica.model.Venta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct para convertir entre Venta, LineaDeVenta, Modificacion y sus DTOs.
 */
@Mapper(config = MapperConfiguration.class)
public interface VentaMapper {

    VentaResponseDto toResponse(Venta entity);

    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "producto.nombre", target = "productoNombre")
    LineaDeVentaResponseDto toLineaResponse(LineaDeVenta linea);

    @Mapping(source = "insumo.id", target = "insumoId")
    @Mapping(source = "insumo.nombre", target = "insumoNombre")
    @Mapping(source = "insumo.unidad", target = "insumoUnidad")
    ModificacionResponseDto toModificacionResponse(Modificacion modificacion);
}
