package com.braiangzz.gestion_gastronomica.service.impl;

import com.braiangzz.gestion_gastronomica.dto.request.LineaDeVentaRequestDto;
import com.braiangzz.gestion_gastronomica.dto.request.ModificacionRequestDto;
import com.braiangzz.gestion_gastronomica.dto.request.VentaRequestDto;
import com.braiangzz.gestion_gastronomica.dto.response.VentaResponseDto;
import com.braiangzz.gestion_gastronomica.exception.BusinessException;
import com.braiangzz.gestion_gastronomica.exception.InvalidRequestException;
import com.braiangzz.gestion_gastronomica.exception.ResourceNotFoundException;
import com.braiangzz.gestion_gastronomica.mapper.VentaMapper;
import com.braiangzz.gestion_gastronomica.model.EstadoVenta;
import com.braiangzz.gestion_gastronomica.model.Ingrediente;
import com.braiangzz.gestion_gastronomica.model.Insumo;
import com.braiangzz.gestion_gastronomica.model.LineaDeVenta;
import com.braiangzz.gestion_gastronomica.model.Modificacion;
import com.braiangzz.gestion_gastronomica.model.Producto;
import com.braiangzz.gestion_gastronomica.model.TipoEntrega;
import com.braiangzz.gestion_gastronomica.model.TipoModificacion;
import com.braiangzz.gestion_gastronomica.model.Venta;
import com.braiangzz.gestion_gastronomica.repository.VentaRepository;
import com.braiangzz.gestion_gastronomica.service.InsumoService;
import com.braiangzz.gestion_gastronomica.service.ProductoService;
import com.braiangzz.gestion_gastronomica.service.VentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementación del servicio de gestión de ventas, cálculo de totales y control de stock.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoService productoService;
    private final InsumoService insumoService;
    private final VentaMapper ventaMapper;

    @Override
    @Transactional
    public VentaResponseDto registrar(VentaRequestDto request) {
        String direccionNormalizada = validarYNormalizarDireccion(request.getTipoEntrega(), request.getDireccion());
        LocalDate fecha = request.getFecha() != null ? request.getFecha() : LocalDate.now();
        BigDecimal descuento = normalizarMoneda(request.getDescuento());

        Venta venta = new Venta();
        venta.setNombreCliente(request.getNombreCliente().trim());
        venta.setFecha(fecha);
        venta.setEstado(EstadoVenta.REGISTRADA);
        venta.setTipoEntrega(request.getTipoEntrega());
        venta.setDireccion(direccionNormalizada);
        venta.setFormaPago(request.getFormaPago());
        venta.setDescuento(descuento);

        Map<Long, Integer> consumoTotalPorInsumo = new LinkedHashMap<>();
        BigDecimal sumaSubtotales = BigDecimal.ZERO;

        for (LineaDeVentaRequestDto lineaDto : request.getLineas()) {
            Producto producto = productoService.obtenerEntidadActivaPorId(lineaDto.getProductoId());
            LineaDeVenta linea = construirLineaDeVenta(producto, lineaDto, consumoTotalPorInsumo);
            venta.agregarLinea(linea);
            sumaSubtotales = sumaSubtotales.add(linea.getSubtotal());
        }

        if (descuento.compareTo(sumaSubtotales) > 0) {
            throw new InvalidRequestException(
                    "El descuento (" + descuento + ") no puede superar el subtotal de la venta (" + sumaSubtotales + ")");
        }

        venta.setTotal(sumaSubtotales.subtract(descuento).setScale(2, RoundingMode.HALF_UP));

        for (Map.Entry<Long, Integer> entry : consumoTotalPorInsumo.entrySet()) {
            insumoService.descontarStock(entry.getKey(), entry.getValue());
        }

        Venta guardada = ventaRepository.save(venta);
        log.info("Venta registrada con id={}, cliente={}, lineas={}, total={}",
                guardada.getId(), guardada.getNombreCliente(), guardada.getLineas().size(), guardada.getTotal());
        return ventaMapper.toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaResponseDto> listar(EstadoVenta estado, LocalDate desde, LocalDate hasta, Pageable pageable) {
        if ((desde != null && hasta == null) || (desde == null && hasta != null)) {
            throw new InvalidRequestException("Debe indicar tanto la fecha 'desde' como la fecha 'hasta'");
        }
        if (desde != null && desde.isAfter(hasta)) {
            throw new InvalidRequestException("La fecha 'desde' no puede ser posterior a la fecha 'hasta'");
        }

        Page<Venta> ventas;
        if (estado != null && desde != null) {
            ventas = ventaRepository.findByEstadoAndFechaBetween(estado, desde, hasta, pageable);
        } else if (estado != null) {
            ventas = ventaRepository.findByEstado(estado, pageable);
        } else if (desde != null) {
            ventas = ventaRepository.findByFechaBetween(desde, hasta, pageable);
        } else {
            ventas = ventaRepository.findAll(pageable);
        }

        return ventas.map(ventaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponseDto obtenerPorId(Long id) {
        Venta venta = buscarPorId(id);
        return ventaMapper.toResponse(venta);
    }

    @Override
    @Transactional
    public VentaResponseDto anular(Long id) {
        Venta venta = buscarPorId(id);
        if (venta.getEstado() == EstadoVenta.ANULADA) {
            throw new BusinessException("La venta con id " + id + " ya se encuentra anulada");
        }

        Map<Long, Integer> devolucionPorInsumo = calcularConsumoDeVenta(venta);
        for (Map.Entry<Long, Integer> entry : devolucionPorInsumo.entrySet()) {
            insumoService.sumarStock(entry.getKey(), entry.getValue());
        }

        venta.setEstado(EstadoVenta.ANULADA);
        Venta actualizada = ventaRepository.save(venta);
        log.info("Venta anulada con id={} y stock restituido en {} insumos", id, devolucionPorInsumo.size());
        return ventaMapper.toResponse(actualizada);
    }

    private Venta buscarPorId(Long id) {
        return ventaRepository.findWithLineasById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id: " + id));
    }

    private String validarYNormalizarDireccion(TipoEntrega tipoEntrega, String direccion) {
        boolean tieneDireccion = direccion != null && !direccion.isBlank();
        if (tipoEntrega == TipoEntrega.DELIVERY && !tieneDireccion) {
            throw new InvalidRequestException("La dirección es obligatoria cuando el tipo de entrega es DELIVERY");
        }
        return tieneDireccion ? direccion.trim() : null;
    }

    private BigDecimal normalizarMoneda(BigDecimal valor) {
        if (valor == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    private LineaDeVenta construirLineaDeVenta(
            Producto producto,
            LineaDeVentaRequestDto lineaDto,
            Map<Long, Integer> consumoTotalPorInsumo) {

        LineaDeVenta linea = new LineaDeVenta();
        linea.setProducto(producto);
        linea.setCantidad(lineaDto.getCantidad());
        linea.setPrecioUnitario(producto.getPrecio());

        Map<Long, Ingrediente> recetaPorInsumoId = new LinkedHashMap<>();
        Map<Long, Integer> consumoUnitario = new LinkedHashMap<>();
        for (Ingrediente ing : producto.getIngredientes()) {
            Long insumoId = ing.getInsumo().getId();
            recetaPorInsumoId.put(insumoId, ing);
            consumoUnitario.put(insumoId, ing.getCantidadConsumida());
        }

        BigDecimal extrasUnitarios = BigDecimal.ZERO;
        List<ModificacionRequestDto> modificacionesDto = lineaDto.getModificaciones();
        if (modificacionesDto != null && !modificacionesDto.isEmpty()) {
            Set<Long> insumosVistos = new HashSet<>();
            for (ModificacionRequestDto modDto : modificacionesDto) {
                if (modDto.getInsumoId() == null || !insumosVistos.add(modDto.getInsumoId())) {
                    throw new InvalidRequestException(
                            "No se puede repetir el mismo insumo en las modificaciones de una misma línea (insumoId: "
                                    + modDto.getInsumoId() + ")");
                }
                extrasUnitarios = extrasUnitarios.add(
                        procesarModificacion(producto, linea, modDto, recetaPorInsumoId, consumoUnitario));
            }
        }

        BigDecimal precioUnitarioFinal = producto.getPrecio().add(extrasUnitarios);
        BigDecimal subtotal = precioUnitarioFinal
                .multiply(BigDecimal.valueOf(lineaDto.getCantidad()))
                .setScale(2, RoundingMode.HALF_UP);
        linea.setSubtotal(subtotal);

        acumularConsumoLinea(consumoUnitario, lineaDto.getCantidad(), consumoTotalPorInsumo);
        return linea;
    }

    private BigDecimal procesarModificacion(
            Producto producto,
            LineaDeVenta linea,
            ModificacionRequestDto modDto,
            Map<Long, Ingrediente> recetaPorInsumoId,
            Map<Long, Integer> consumoUnitario) {

        String descripcion = (modDto.getDescripcion() != null && !modDto.getDescripcion().isBlank())
                ? modDto.getDescripcion().trim()
                : null;

        if (modDto.getTipoModificacion() == TipoModificacion.AGREGAR) {
            if (modDto.getCantidadExtra() == null || modDto.getCantidadExtra() <= 0) {
                throw new InvalidRequestException(
                        "La cantidad extra es obligatoria y debe ser mayor a cero al agregar un insumo");
            }
            Insumo insumo = insumoService.obtenerEntidadActivaPorId(modDto.getInsumoId());
            BigDecimal precioExtra = normalizarMoneda(modDto.getPrecioExtra());

            consumoUnitario.merge(insumo.getId(), modDto.getCantidadExtra(), Integer::sum);
            linea.agregarModificacion(new Modificacion(
                    null, linea, insumo, TipoModificacion.AGREGAR,
                    modDto.getCantidadExtra(), precioExtra, descripcion));
            return precioExtra;
        }

        Ingrediente ingredienteReceta = recetaPorInsumoId.get(modDto.getInsumoId());
        if (ingredienteReceta == null) {
            throw new InvalidRequestException(
                    "No se puede quitar el insumo con id " + modDto.getInsumoId()
                            + " porque no forma parte de la receta del producto '" + producto.getNombre() + "'");
        }
        if (modDto.getPrecioExtra() != null && modDto.getPrecioExtra().compareTo(BigDecimal.ZERO) > 0) {
            throw new InvalidRequestException("Las modificaciones de tipo QUITAR no pueden tener precio extra");
        }

        int cantidadBase = ingredienteReceta.getCantidadConsumida();
        int cantidadAQuitar = modDto.getCantidadExtra() != null ? modDto.getCantidadExtra() : cantidadBase;
        if (cantidadAQuitar > cantidadBase) {
            throw new InvalidRequestException(
                    "La cantidad a quitar (" + cantidadAQuitar + ") supera la cantidad en la receta ("
                            + cantidadBase + ") para el insumo '" + ingredienteReceta.getInsumo().getNombre() + "'");
        }

        consumoUnitario.put(ingredienteReceta.getInsumo().getId(), cantidadBase - cantidadAQuitar);
        BigDecimal precioCero = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        linea.agregarModificacion(new Modificacion(
                null, linea, ingredienteReceta.getInsumo(), TipoModificacion.QUITAR,
                cantidadAQuitar, precioCero, descripcion));
        return precioCero;
    }

    private void acumularConsumoLinea(
            Map<Long, Integer> consumoUnitario,
            int cantidadProducto,
            Map<Long, Integer> consumoTotalPorInsumo) {

        for (Map.Entry<Long, Integer> entry : consumoUnitario.entrySet()) {
            int cantidadUnitaria = entry.getValue();
            if (cantidadUnitaria > 0) {
                consumoTotalPorInsumo.merge(entry.getKey(), cantidadUnitaria * cantidadProducto, Integer::sum);
            }
        }
    }

    private Map<Long, Integer> calcularConsumoDeVenta(Venta venta) {
        Map<Long, Integer> consumoTotal = new LinkedHashMap<>();
        for (LineaDeVenta linea : venta.getLineas()) {
            Map<Long, Integer> consumoUnitario = new LinkedHashMap<>();
            for (Ingrediente ing : linea.getProducto().getIngredientes()) {
                consumoUnitario.put(ing.getInsumo().getId(), ing.getCantidadConsumida());
            }

            for (Modificacion mod : linea.getModificaciones()) {
                Long insumoId = mod.getInsumo().getId();
                if (mod.getTipoModificacion() == TipoModificacion.AGREGAR) {
                    consumoUnitario.merge(insumoId, mod.getCantidadExtra(), Integer::sum);
                } else {
                    int actual = consumoUnitario.getOrDefault(insumoId, 0);
                    consumoUnitario.put(insumoId, Math.max(0, actual - mod.getCantidadExtra()));
                }
            }

            acumularConsumoLinea(consumoUnitario, linea.getCantidad(), consumoTotal);
        }
        return consumoTotal;
    }
}
