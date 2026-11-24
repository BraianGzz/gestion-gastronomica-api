package com.braiangzz.gestion_gastronomica.mapper;

import com.braiangzz.gestion_gastronomica.dto.request.CompraRequestDto;
import com.braiangzz.gestion_gastronomica.dto.response.CompraResponseDto;
import com.braiangzz.gestion_gastronomica.mapper.config.MapperConfiguration;
import com.braiangzz.gestion_gastronomica.model.Compra;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct para convertir entre la entidad Compra y sus DTOs.
 */
@Mapper(config = MapperConfiguration.class)
public interface CompraMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "insumo", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Compra toEntity(CompraRequestDto dto);

    @Mapping(source = "insumo.id", target = "insumoId")
    @Mapping(source = "insumo.nombre", target = "insumoNombre")
    @Mapping(source = "insumo.unidad", target = "insumoUnidad")
    CompraResponseDto toResponse(Compra entity);
}
