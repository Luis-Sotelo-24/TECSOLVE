package com.techsolvers.ecommerce.service;

import com.techsolvers.ecommerce.dto.OrdenItemDTO;
import com.techsolvers.ecommerce.model.OrdenItem;
import com.techsolvers.ecommerce.model.Orden;
import com.techsolvers.ecommerce.model.Producto;
import com.techsolvers.ecommerce.model.Usuario;
import com.techsolvers.ecommerce.repository.OrdenItemRepository;
import com.techsolvers.ecommerce.repository.OrdenRepository;
import com.techsolvers.ecommerce.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdenItemService {

    @Autowired
    private OrdenItemRepository ordenItemRepository;
    
    @Autowired
    private OrdenRepository ordenRepository;
    
    @Autowired
    private ProductoRepository productoRepository;

    // Obtener items por orden
    public List<OrdenItemDTO> obtenerPorOrden(Long ordenId) {
        try {
            Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

            List<OrdenItem> items = ordenItemRepository.findByOrden(orden);
            return items.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener items de la orden: " + e.getMessage());
        }
    }

    // Obtener item por ID
    public OrdenItemDTO obtenerPorId(Long id) {
        try {
            OrdenItem item = ordenItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));
            return convertirADTO(item);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener item: " + e.getMessage());
        }
    }

    // Obtener items por producto
    public List<OrdenItemDTO> obtenerPorProducto(Long productoId) {
        try {
            Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            List<OrdenItem> items = ordenItemRepository.findByProducto(producto);
            return items.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener items por producto: " + e.getMessage());
        }
    }

    // Obtener productos más vendidos
    public List<java.util.Map<String, Object>> obtenerProductosMasVendidos() {
        try {
            List<Object[]> resultados = ordenItemRepository.findProductosMasVendidos();
            return resultados.stream()
                .map(row -> {
                    java.util.Map<String, Object> item = new java.util.HashMap<>();
                    Producto producto = (Producto) row[0];
                    Long totalVendido = (Long) row[1];
                    
                    item.put("productoId", producto.getId());
                    item.put("productoNombre", producto.getNombre());
                    item.put("productoMarca", producto.getMarca());
                    item.put("productoModelo", producto.getModelo());
                    item.put("totalVendido", totalVendido);
                    item.put("imagenPrincipal", producto.getImagenPrincipal());
                    
                    return item;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener productos más vendidos: " + e.getMessage());
        }
    }

    // Obtener productos más vendidos en período
    public List<java.util.Map<String, Object>> obtenerProductosMasVendidosPorPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        try {
            List<Object[]> resultados = ordenItemRepository.findProductosMasVendidosPorPeriodo(fechaInicio, fechaFin);
            return resultados.stream()
                .map(row -> {
                    java.util.Map<String, Object> item = new java.util.HashMap<>();
                    Producto producto = (Producto) row[0];
                    Long totalVendido = (Long) row[1];
                    
                    item.put("productoId", producto.getId());
                    item.put("productoNombre", producto.getNombre());
                    item.put("productoMarca", producto.getMarca());
                    item.put("totalVendido", totalVendido);
                    item.put("fechaInicio", fechaInicio);
                    item.put("fechaFin", fechaFin);
                    
                    return item;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener productos más vendidos por período: " + e.getMessage());
        }
    }

    // Obtener ingresos por producto
    public List<java.util.Map<String, Object>> obtenerIngresosPorProducto() {
        try {
            List<Object[]> resultados = ordenItemRepository.findIngresosPorProducto();
            return resultados.stream()
                .map(row -> {
                    java.util.Map<String, Object> item = new java.util.HashMap<>();
                    Producto producto = (Producto) row[0];
                    java.math.BigDecimal totalIngresos = (java.math.BigDecimal) row[1];
                    
                    item.put("productoId", producto.getId());
                    item.put("productoNombre", producto.getNombre());
                    item.put("productoMarca", producto.getMarca());
                    item.put("totalIngresos", totalIngresos);
                    item.put("imagenPrincipal", producto.getImagenPrincipal());
                    
                    return item;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener ingresos por producto: " + e.getMessage());
        }
    }

    // Calcular cantidad total vendida de un producto
    public Integer calcularCantidadVendidaDeProducto(Long productoId) {
        try {
            Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            Integer cantidadVendida = ordenItemRepository.calcularCantidadVendidaDeProducto(producto);
            return cantidadVendida != null ? cantidadVendida : 0;

        } catch (Exception e) {
            throw new RuntimeException("Error al calcular cantidad vendida: " + e.getMessage());
        }
    }

    // Obtener items vendidos hoy
    public List<OrdenItemDTO> obtenerItemsVendidosHoy() {
        try {
            List<OrdenItem> items = ordenItemRepository.findItemsVendidosHoy();
            return items.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener items vendidos hoy: " + e.getMessage());
        }
    }

    // Obtener productos vendidos por trabajador
    public List<java.util.Map<String, Object>> obtenerProductosVendidosPorTrabajador(Long trabajadorId) {
        try {
            Usuario trabajador = new Usuario();
            trabajador.setId(trabajadorId);
            
            List<Object[]> resultados = ordenItemRepository.findProductosVendidosPorTrabajador(trabajador);
            return resultados.stream()
                .map(row -> {
                    java.util.Map<String, Object> item = new java.util.HashMap<>();
                    Producto producto = (Producto) row[0];
                    Long cantidad = (Long) row[1];
                    
                    item.put("productoId", producto.getId());
                    item.put("productoNombre", producto.getNombre());
                    item.put("cantidadVendida", cantidad);
                    item.put("trabajadorId", trabajadorId);
                    
                    return item;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener productos vendidos por trabajador: " + e.getMessage());
        }
    }

    // Obtener estadísticas de ventas por marca
    public List<java.util.Map<String, Object>> obtenerEstadisticasVentasPorMarca() {
        try {
            List<Object[]> resultados = ordenItemRepository.estadisticasVentasPorMarca();
            return resultados.stream()
                .map(row -> {
                    java.util.Map<String, Object> item = new java.util.HashMap<>();
                    String marca = (String) row[0];
                    Long cantidadOrdenes = (Long) row[1];
                    Long cantidadProductos = (Long) row[2];
                    java.math.BigDecimal totalVentas = (java.math.BigDecimal) row[3];
                    
                    item.put("marca", marca);
                    item.put("cantidadOrdenes", cantidadOrdenes);
                    item.put("cantidadProductos", cantidadProductos);
                    item.put("totalVentas", totalVentas);
                    
                    return item;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener estadísticas por marca: " + e.getMessage());
        }
    }

    // Calcular estadísticas de la orden
    public java.util.Map<String, Object> calcularEstadisticasOrden(Long ordenId) {
        try {
            Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

            java.util.Map<String, Object> estadisticas = new java.util.HashMap<>();
            
            long totalItems = ordenItemRepository.contarItemsEnOrden(orden);
            Integer cantidadTotal = ordenItemRepository.calcularCantidadTotalEnOrden(orden);
            java.math.BigDecimal totalOrden = ordenItemRepository.calcularTotalOrden(orden);
            
            estadisticas.put("totalItems", totalItems);
            estadisticas.put("cantidadTotal", cantidadTotal != null ? cantidadTotal : 0);
            estadisticas.put("totalOrden", totalOrden != null ? totalOrden : java.math.BigDecimal.ZERO);
            estadisticas.put("numeroOrden", orden.getNumeroOrden());
            estadisticas.put("estadoOrden", orden.getEstado());
            
            return estadisticas;
        } catch (Exception e) {
            throw new RuntimeException("Error al calcular estadísticas de orden: " + e.getMessage());
        }
    }

    // Obtener resumen de ventas del día
    public java.util.Map<String, Object> obtenerResumenVentasDelDia() {
        try {
            List<OrdenItem> itemsHoy = ordenItemRepository.findItemsVendidosHoy();
            
            java.util.Map<String, Object> resumen = new java.util.HashMap<>();
            
            int totalItems = itemsHoy.size();
            int totalCantidad = itemsHoy.stream().mapToInt(OrdenItem::getCantidad).sum();
            java.math.BigDecimal totalVentas = itemsHoy.stream()
                .map(OrdenItem::getSubtotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            
            resumen.put("fecha", LocalDateTime.now().toLocalDate());
            resumen.put("totalItems", totalItems);
            resumen.put("totalCantidad", totalCantidad);
            resumen.put("totalVentas", totalVentas);
            
            return resumen;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener resumen de ventas del día: " + e.getMessage());
        }
    }

    // Convertir Entity a DTO
    private OrdenItemDTO convertirADTO(OrdenItem item) {
        OrdenItemDTO dto = new OrdenItemDTO();
        dto.setId(item.getId());
        dto.setCantidad(item.getCantidad());
        dto.setPrecioUnitario(item.getPrecioUnitario());
        dto.setSubtotal(item.getSubtotal());
        
        // Datos del producto guardados
        dto.setProductoNombre(item.getProductoNombre());
        dto.setProductoDescripcion(item.getProductoDescripcion());
        dto.setProductoMarca(item.getProductoMarca());
        dto.setProductoModelo(item.getProductoModelo());
        
        // Datos actuales del producto
        if (item.getProducto() != null) {
            dto.setProductoId(item.getProducto().getId());
            dto.setProductoImagenPrincipal(item.getProducto().getImagenPrincipal());
            dto.setProductoPrecioActual(item.getProducto().getPrecio());
            dto.setProductoStockActual(item.getProducto().getStock());
        }
        
        // Datos de la orden
        if (item.getOrden() != null) {
            dto.setOrdenId(item.getOrden().getId());
            dto.setNumeroOrden(item.getOrden().getNumeroOrden());
        }
        
        return dto;
    }
}





