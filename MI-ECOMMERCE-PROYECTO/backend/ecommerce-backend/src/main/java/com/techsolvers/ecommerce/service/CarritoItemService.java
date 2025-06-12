package com.techsolvers.ecommerce.service;

import com.techsolvers.ecommerce.dto.CarritoItemDTO;
import com.techsolvers.ecommerce.model.CarritoItem;
import com.techsolvers.ecommerce.model.Carrito;
import com.techsolvers.ecommerce.model.Producto;
import com.techsolvers.ecommerce.repository.CarritoItemRepository;
import com.techsolvers.ecommerce.repository.CarritoRepository;
import com.techsolvers.ecommerce.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarritoItemService {

    @Autowired
    private CarritoItemRepository carritoItemRepository;
    
    @Autowired
    private CarritoRepository carritoRepository;
    
    @Autowired
    private ProductoRepository productoRepository;

    // Obtener items por carrito
    public List<CarritoItemDTO> obtenerPorCarrito(Long carritoId) {
        try {
            Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

            List<CarritoItem> items = carritoItemRepository.findByCarrito(carrito);
            return items.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener items del carrito: " + e.getMessage());
        }
    }

    // Obtener item por ID
    public CarritoItemDTO obtenerPorId(Long id) {
        try {
            CarritoItem item = carritoItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));
            return convertirADTO(item);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener item: " + e.getMessage());
        }
    }

    // Verificar si producto está en carrito
    public boolean existeEnCarrito(Long carritoId, Long productoId) {
        try {
            Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
                
            Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            return carritoItemRepository.existsByCarritoAndProducto(carrito, producto);

        } catch (Exception e) {
            throw new RuntimeException("Error al verificar item en carrito: " + e.getMessage());
        }
    }

    // Actualizar cantidad directamente
    public CarritoItemDTO actualizarCantidad(Long itemId, Integer nuevaCantidad) {
        try {
            CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

            if (nuevaCantidad <= 0) {
                throw new RuntimeException("La cantidad debe ser mayor a 0");
            }

            // Verificar stock disponible
            if (!item.getProducto().hayStock(nuevaCantidad)) {
                throw new RuntimeException("Stock insuficiente. Stock disponible: " + item.getProducto().getStock());
            }

            item.setCantidad(nuevaCantidad);
            item.calcularSubtotal();
            CarritoItem itemActualizado = carritoItemRepository.save(item);

            // Actualizar total del carrito
            Carrito carrito = item.getCarrito();
            carrito.calcularTotal();
            carritoRepository.save(carrito);

            return convertirADTO(itemActualizado);

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar cantidad: " + e.getMessage());
        }
    }

    // Eliminar item
    public boolean eliminar(Long itemId) {
        try {
            CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

            Carrito carrito = item.getCarrito();
            carritoItemRepository.delete(item);

            // Actualizar total del carrito
            carrito.calcularTotal();
            carritoRepository.save(carrito);

            return true;

        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar item: " + e.getMessage());
        }
    }

    // Obtener items con cantidad mayor a X
    public List<CarritoItemDTO> obtenerItemsConCantidadMayorA(Integer cantidad) {
        try {
            List<CarritoItem> items = carritoItemRepository.findItemsConCantidadMayorA(cantidad);
            return items.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener items con cantidad mayor: " + e.getMessage());
        }
    }

    // Obtener productos más agregados al carrito
    public List<java.util.Map<String, Object>> obtenerProductosMasAgregados() {
        try {
            List<Object[]> resultados = carritoItemRepository.findProductosMasAgregados();
            return resultados.stream()
                .map(row -> {
                    java.util.Map<String, Object> item = new java.util.HashMap<>();
                    Producto producto = (Producto) row[0];
                    Long cantidad = (Long) row[1];
                    
                    item.put("productoId", producto.getId());
                    item.put("productoNombre", producto.getNombre());
                    item.put("productoMarca", producto.getMarca());
                    item.put("cantidadAgregada", cantidad);
                    
                    return item;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener productos más agregados: " + e.getMessage());
        }
    }

    // Calcular estadísticas del carrito
    public java.util.Map<String, Object> calcularEstadisticasCarrito(Long carritoId) {
        try {
            Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

            java.util.Map<String, Object> estadisticas = new java.util.HashMap<>();
            
            long totalItems = carritoItemRepository.contarItemsEnCarrito(carrito);
            Integer cantidadTotal = carritoItemRepository.calcularCantidadTotalEnCarrito(carrito);
            java.math.BigDecimal totalCarrito = carritoItemRepository.calcularTotalCarrito(carrito);
            
            estadisticas.put("totalItems", totalItems);
            estadisticas.put("cantidadTotal", cantidadTotal != null ? cantidadTotal : 0);
            estadisticas.put("totalCarrito", totalCarrito != null ? totalCarrito : java.math.BigDecimal.ZERO);
            
            return estadisticas;
        } catch (Exception e) {
            throw new RuntimeException("Error al calcular estadísticas: " + e.getMessage());
        }
    }

    // Validar disponibilidad de todos los items del carrito
    public java.util.Map<String, Object> validarDisponibilidad(Long carritoId) {
        try {
            Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

            List<CarritoItem> items = carritoItemRepository.findByCarrito(carrito);
            java.util.Map<String, Object> resultado = new java.util.HashMap<>();
            
            boolean todosDisponibles = true;
            java.util.List<String> problemas = new java.util.ArrayList<>();
            
            for (CarritoItem item : items) {
                Producto producto = item.getProducto();
                if (!producto.hayStock(item.getCantidad())) {
                    todosDisponibles = false;
                    problemas.add(producto.getNombre() + " - Stock disponible: " + producto.getStock() + 
                                ", Solicitado: " + item.getCantidad());
                }
            }
            
            resultado.put("todosDisponibles", todosDisponibles);
            resultado.put("problemas", problemas);
            
            return resultado;
        } catch (Exception e) {
            throw new RuntimeException("Error al validar disponibilidad: " + e.getMessage());
        }
    }

    // Convertir Entity a DTO
    private CarritoItemDTO convertirADTO(CarritoItem item) {
        CarritoItemDTO dto = new CarritoItemDTO();
        dto.setId(item.getId());
        dto.setCantidad(item.getCantidad());
        dto.setPrecioUnitario(item.getPrecioUnitario());
        dto.setSubtotal(item.getSubtotal());
        dto.setFechaAgregado(item.getFechaAgregado());
        dto.setCarritoId(item.getCarrito().getId());
        
        // Datos del producto
        if (item.getProducto() != null) {
            Producto producto = item.getProducto();
            dto.setProductoId(producto.getId());
            dto.setProductoNombre(producto.getNombre());
            dto.setProductoDescripcion(producto.getDescripcion());
            dto.setProductoImagenPrincipal(producto.getImagenPrincipal());
            dto.setProductoMarca(producto.getMarca());
            dto.setProductoModelo(producto.getModelo());
            dto.setProductoStock(producto.getStock());
            dto.setProductoPrecioActual(producto.getPrecio());
            
            if (producto.getCategoria() != null) {
                dto.setCategoriaTitulo(producto.getCategoria().getTitulo());
            }
        }
        
        return dto;
    }
}

