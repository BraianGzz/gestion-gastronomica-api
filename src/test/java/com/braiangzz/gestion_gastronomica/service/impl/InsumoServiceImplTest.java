package com.braiangzz.gestion_gastronomica.service.impl;

import com.braiangzz.gestion_gastronomica.dto.request.AjusteStockRequestDto;
import com.braiangzz.gestion_gastronomica.dto.request.InsumoRequestDto;
import com.braiangzz.gestion_gastronomica.dto.response.InsumoResponseDto;
import com.braiangzz.gestion_gastronomica.exception.BusinessException;
import com.braiangzz.gestion_gastronomica.exception.InvalidRequestException;
import com.braiangzz.gestion_gastronomica.exception.ResourceNotFoundException;
import com.braiangzz.gestion_gastronomica.mapper.InsumoMapper;
import com.braiangzz.gestion_gastronomica.model.Insumo;
import com.braiangzz.gestion_gastronomica.model.UnidadMedida;
import com.braiangzz.gestion_gastronomica.repository.InsumoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InsumoServiceImplTest {

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private InsumoMapper insumoMapper;

    @InjectMocks
    private InsumoServiceImpl insumoService;

    private Insumo insumo;
    private InsumoRequestDto requestDto;
    private InsumoResponseDto responseDto;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        insumo = new Insumo(1L, "Carne", 2000, UnidadMedida.GRAMOS, true, now, now);
        requestDto = new InsumoRequestDto("Carne", 2000, UnidadMedida.GRAMOS);
        responseDto = new InsumoResponseDto(1L, "Carne", 2000, UnidadMedida.GRAMOS, true, now, now);
    }

    @Test
    void crear_datosValidos_retornaInsumoResponseDto() {
        // Given
        when(insumoRepository.existsByNombreIgnoreCase("Carne")).thenReturn(false);
        when(insumoMapper.toEntity(requestDto)).thenReturn(insumo);
        when(insumoRepository.save(insumo)).thenReturn(insumo);
        when(insumoMapper.toResponse(insumo)).thenReturn(responseDto);

        // When
        InsumoResponseDto resultado = insumoService.crear(requestDto);

        // Then
        assertThat(resultado.getNombre()).isEqualTo("Carne");
        verify(insumoRepository).save(insumo);
    }

    @Test
    void crear_nombreDuplicado_lanzaBusinessException() {
        // Given
        when(insumoRepository.existsByNombreIgnoreCase("Carne")).thenReturn(true);

        // When / Then
        assertThrows(BusinessException.class, () -> insumoService.crear(requestDto));
        verify(insumoRepository, never()).save(any());
    }

    @Test
    void obtenerPorId_noExistente_lanzaResourceNotFoundException() {
        // Given
        when(insumoRepository.findByIdAndActivoTrue(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> insumoService.obtenerPorId(99L));
    }

    @Test
    void ajustarStock_existente_actualizaCantidad() {
        // Given
        AjusteStockRequestDto ajusteDto = new AjusteStockRequestDto(1500, "Conteo manual");
        when(insumoRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(insumo));
        when(insumoRepository.save(insumo)).thenReturn(insumo);
        when(insumoMapper.toResponse(insumo)).thenReturn(responseDto);

        // When
        insumoService.ajustarStock(1L, ajusteDto);

        // Then
        assertThat(insumo.getCantidadStock()).isEqualTo(1500);
        verify(insumoRepository).save(insumo);
    }

    @Test
    void eliminar_existente_realizaBajaLogica() {
        // Given
        when(insumoRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(insumo));

        // When
        insumoService.eliminar(1L);

        // Then
        assertThat(insumo.getActivo()).isFalse();
        verify(insumoRepository).save(insumo);
    }

    @Test
    void sumarStock_cantidadValida_incrementaStock() {
        // Given
        when(insumoRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(insumo));
        when(insumoRepository.save(insumo)).thenReturn(insumo);

        // When
        Insumo resultado = insumoService.sumarStock(1L, 500);

        // Then
        assertThat(resultado.getCantidadStock()).isEqualTo(2500);
        verify(insumoRepository).save(insumo);
    }

    @Test
    void sumarStock_cantidadNoPositiva_lanzaInvalidRequestException() {
        // When / Then
        assertThrows(InvalidRequestException.class, () -> insumoService.sumarStock(1L, 0));
        verify(insumoRepository, never()).save(any());
    }

    @Test
    void descontarStock_stockSuficiente_decrementaStock() {
        // Given
        when(insumoRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(insumo));
        when(insumoRepository.save(insumo)).thenReturn(insumo);

        // When
        Insumo resultado = insumoService.descontarStock(1L, 500);

        // Then
        assertThat(resultado.getCantidadStock()).isEqualTo(1500);
        verify(insumoRepository).save(insumo);
    }

    @Test
    void descontarStock_stockInsuficiente_lanzaBusinessException() {
        // Given
        when(insumoRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(insumo));

        // When / Then
        assertThrows(BusinessException.class, () -> insumoService.descontarStock(1L, 2500));
        verify(insumoRepository, never()).save(any());
    }
}
