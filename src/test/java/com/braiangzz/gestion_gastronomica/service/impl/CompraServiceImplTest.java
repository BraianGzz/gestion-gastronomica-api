package com.braiangzz.gestion_gastronomica.service.impl;

import com.braiangzz.gestion_gastronomica.dto.request.CompraRequestDto;
import com.braiangzz.gestion_gastronomica.dto.response.CompraResponseDto;
import com.braiangzz.gestion_gastronomica.exception.BusinessException;
import com.braiangzz.gestion_gastronomica.exception.InvalidRequestException;
import com.braiangzz.gestion_gastronomica.exception.ResourceNotFoundException;
import com.braiangzz.gestion_gastronomica.mapper.CompraMapper;
import com.braiangzz.gestion_gastronomica.model.Compra;
import com.braiangzz.gestion_gastronomica.model.Insumo;
import com.braiangzz.gestion_gastronomica.model.UnidadMedida;
import com.braiangzz.gestion_gastronomica.repository.CompraRepository;
import com.braiangzz.gestion_gastronomica.service.InsumoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompraServiceImplTest {

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private InsumoService insumoService;

    @Mock
    private CompraMapper compraMapper;

    @InjectMocks
    private CompraServiceImpl compraService;

    private Insumo insumo;
    private Compra compra;
    private CompraRequestDto requestDto;
    private CompraResponseDto responseDto;

    @BeforeEach
    void setUp() {
        LocalDate hoy = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        insumo = new Insumo(1L, "Carne", 3000, UnidadMedida.GRAMOS, true, now, now);
        compra = new Compra(10L, insumo, hoy, 1000, new BigDecimal("15000.00"), now, now);
        requestDto = new CompraRequestDto(1L, hoy, 1000, new BigDecimal("15000.00"));
        responseDto = new CompraResponseDto(10L, 1L, "Carne", UnidadMedida.GRAMOS, hoy, 1000, new BigDecimal("15000.00"), now);
    }

    @Test
    void registrar_datosValidos_sumaStockYGuardaCompra() {
        // Given
        when(insumoService.sumarStock(1L, 1000)).thenReturn(insumo);
        when(compraMapper.toEntity(requestDto)).thenReturn(compra);
        when(compraRepository.save(compra)).thenReturn(compra);
        when(compraMapper.toResponse(compra)).thenReturn(responseDto);

        // When
        CompraResponseDto resultado = compraService.registrar(requestDto);

        // Then
        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getInsumoNombre()).isEqualTo("Carne");
        verify(insumoService).sumarStock(1L, 1000);
        verify(compraRepository).save(compra);
    }

    @Test
    void registrar_insumoInexistente_lanzaResourceNotFoundException() {
        // Given
        when(insumoService.sumarStock(1L, 1000))
                .thenThrow(new ResourceNotFoundException("Insumo no encontrado con id: 1"));

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> compraService.registrar(requestDto));
        verify(compraRepository, never()).save(any());
    }

    @Test
    void listar_rangoFechasInvalido_lanzaInvalidRequestException() {
        // Given
        LocalDate desde = LocalDate.of(2026, 7, 20);
        LocalDate hasta = LocalDate.of(2026, 7, 10);

        // When / Then
        assertThrows(InvalidRequestException.class,
                () -> compraService.listar(null, desde, hasta, PageRequest.of(0, 10)));
    }

    @Test
    void listar_soloUnaFecha_lanzaInvalidRequestException() {
        // Given
        LocalDate desde = LocalDate.of(2026, 7, 10);

        // When / Then
        assertThrows(InvalidRequestException.class,
                () -> compraService.listar(null, desde, null, PageRequest.of(0, 10)));
    }

    @Test
    void eliminar_compraExistente_revierteStockYElimina() {
        // Given
        when(compraRepository.findWithInsumoById(10L)).thenReturn(Optional.of(compra));

        // When
        compraService.eliminar(10L);

        // Then
        verify(insumoService).descontarStock(1L, 1000);
        verify(compraRepository).delete(compra);
    }

    @Test
    void eliminar_stockInsuficiente_lanzaBusinessExceptionYNoElimina() {
        // Given
        when(compraRepository.findWithInsumoById(10L)).thenReturn(Optional.of(compra));
        doThrow(new BusinessException("Stock insuficiente")).when(insumoService).descontarStock(1L, 1000);

        // When / Then
        assertThrows(BusinessException.class, () -> compraService.eliminar(10L));
        verify(compraRepository, never()).delete(any());
    }

    @Test
    void eliminar_compraInexistente_lanzaResourceNotFoundException() {
        // Given
        when(compraRepository.findWithInsumoById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> compraService.eliminar(99L));
        verify(insumoService, never()).descontarStock(any(), any());
    }
}
