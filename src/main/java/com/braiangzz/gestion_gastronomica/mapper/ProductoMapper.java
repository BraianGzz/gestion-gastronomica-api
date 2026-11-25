package com.braiangzz.gestion_gastronomica.mapper;

import com.braiangzz.gestion_gastronomica.dto.request.ProductoRequestDto;
import com.braiangzz.gestion_gastronomica.dto.response.IngredienteResponseDto;
import com.braiangzz.gestion_gastronomica.dto.response.ProductoResponseDto;
import com.braiangzz.gestion_gastronomica.mapper.config.MapperConfiguration;
import com.braiangzz.gestion_gastronomica.model.Ingrediente;
import com.braiangzz.gestion_gastronomica.model.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper MapStruct para convertir entre Producto/Ingrediente y sus DTOs.
 */
@Mapper(config = MapperConfiguration.class)
public interface ProductoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "ingredientes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Producto toEntity(ProductoRequestDto dto);

    ProductoResponseDto toResponse(Producto entity);

    @Mapping(source = "insumo.id", target = "insumoId")
    @Mapping(source = "insumo.nombre", target = "insumoNombre")
    @Mapping(source = "insumo.unidad", target = "insumoUnidad")
    IngredienteResponseDto toIngredienteResponse(Ingrediente ingrediente);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "ingredientes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(ProductoRequestDto dto, @MappingTarget Producto entity);
}
