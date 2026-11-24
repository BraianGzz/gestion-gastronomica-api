package com.braiangzz.gestion_gastronomica.mapper;

import com.braiangzz.gestion_gastronomica.dto.request.InsumoRequestDto;
import com.braiangzz.gestion_gastronomica.dto.response.InsumoResponseDto;
import com.braiangzz.gestion_gastronomica.mapper.config.MapperConfiguration;
import com.braiangzz.gestion_gastronomica.model.Insumo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Mapper MapStruct para convertir entre la entidad Insumo y sus DTOs.
 */
@Mapper(config = MapperConfiguration.class)
public interface InsumoMapper {

    Insumo toEntity(InsumoRequestDto dto);

    InsumoResponseDto toResponse(Insumo entity);

    void updateEntity(InsumoRequestDto dto, @MappingTarget Insumo entity);
}
