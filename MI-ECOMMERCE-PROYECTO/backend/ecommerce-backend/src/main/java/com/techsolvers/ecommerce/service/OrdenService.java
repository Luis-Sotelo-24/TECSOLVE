package com.techsolvers.ecommerce.service;

import com.techsolvers.ecommerce.dto.OrdenDTO;
import com.techsolvers.ecommerce.dto.OrdenItemDTO;
import com.techsolvers.ecommerce.model.*;
import com.techsolvers.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;
    
    @Autowired
    private OrdenItemRepository ordenItemRepository;
    
    @Autowired
    private CarritoRepository carritoRepository;
    
    //Me mandaba error esto xd
    //@Autowired
    //private CarritoItemRepository carritoItemRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Crear orden desde carrito
    public OrdenDTO crearOrdenDesdeCarrito(Long carritoId, String metodoPago) {
        try {
            Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

            if (carrito.getItems().isEmpty()) {
                throw new RuntimeException("El carrito está vacío");
            }

            // Verificar stock de todos los productos
            for (CarritoItem item : carrito.getItems()) {
                if (!item.getProducto().hayStock(item.getCantidad())) {
                    throw new RuntimeException("Stock insuficiente para: " + item.getProducto().getNombre());
                }
            }

            // Crear la orden
            Orden orden = Orden.crearDesdeCarrito(carrito, metodoPago);
            orden = ordenRepository.save(orden);

            // Crear los items de la orden y reducir stock
            for (CarritoItem carritoItem : carrito.getItems()) {
                OrdenItem ordenItem = new OrdenItem(
                    orden,
                    carritoItem.getProducto(),
                    carritoItem.getCantidad(),
                    carritoItem.getPrecioUnitario()
                );
                ordenItemRepository.save(ordenItem);

                // Reducir stock del producto
                Producto producto = carritoItem.getProducto();
                producto.reducirStock(carritoItem.getCantidad());
                productoRepository.save(producto);
            }

            // Cambiar estado del carrito a completado
            carrito.setEstado("completado");
            carritoRepository.save(carrito);

            return convertirADTO(orden);

        } catch (Exception e) {
            throw new RuntimeException("Error al crear orden: " + e.getMessage());
        }
    }

    // Obtener orden por ID
    public OrdenDTO obtenerPorId(Long id) {
        try {
            Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
            return convertirADTO(orden);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener orden: " + e.getMessage());
        }
    }

    // Obtener orden por número
    public OrdenDTO obtenerPorNumero(String numeroOrden) {
        try {
            Orden orden = ordenRepository.findByNumeroOrden(numeroOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
            return convertirADTO(orden);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener orden: " + e.getMessage());
        }
    }

    // Obtener órdenes por usuario
    public List<OrdenDTO> obtenerPorUsuario(Long usuarioId) {
        try {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<Orden> ordenes = ordenRepository.findByUsuarioOrderByFechaCreacionDesc(usuario);
            return ordenes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener órdenes del usuario: " + e.getMessage());
        }
    }

    // Obtener órdenes por estado
    public List<OrdenDTO> obtenerPorEstado(String estado) {
        try {
            List<Orden> ordenes = ordenRepository.findByEstadoOrderByFechaCreacionDesc(estado);
            return ordenes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener órdenes por estado: " + e.getMessage());
        }
    }

    // Obtener órdenes pendientes
    public List<OrdenDTO> obtenerOrdenesPendientes() {
        try {
            List<Orden> ordenes = ordenRepository.findOrdenesPendientes();
            return ordenes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener órdenes pendientes: " + e.getMessage());
        }
    }

    // Obtener últimas órdenes
    public List<OrdenDTO> obtenerUltimasOrdenes(int limite) {
        try {
            List<Orden> ordenes = ordenRepository.findUltimasOrdenes();
            return ordenes.stream()
                .limit(limite)
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener últimas órdenes: " + e.getMessage());
        }
    }

    // Actualizar estado de orden
    public OrdenDTO actualizarEstado(Long id, String nuevoEstado) {
        try {
            Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

            String estadoAnterior = orden.getEstado();
            orden.setEstado(nuevoEstado);

            // Si cambia a entregado, establecer fecha de entrega
            if ("entregado".equals(nuevoEstado) && !"entregado".equals(estadoAnterior)) {
                orden.setFechaEntrega(LocalDateTime.now());
            }

            Orden ordenActualizada = ordenRepository.save(orden);
            return convertirADTO(ordenActualizada);

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar estado: " + e.getMessage());
        }
    }

    // Cancelar orden
    public OrdenDTO cancelarOrden(Long id, String motivo) {
        try {
            Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

            if ("entregado".equals(orden.getEstado())) {
                throw new RuntimeException("No se puede cancelar una orden ya entregada");
            }

            // Restaurar stock de los productos
            for (OrdenItem item : orden.getItems()) {
                Producto producto = item.getProducto();
                producto.aumentarStock(item.getCantidad());
                productoRepository.save(producto);
            }

            orden.setEstado("cancelado");
            if (motivo != null) {
                orden.setNotas(orden.getNotas() != null ? 
                    orden.getNotas() + "\nCancelado: " + motivo : "Cancelado: " + motivo);
            }

            Orden ordenCancelada = ordenRepository.save(orden);
            return convertirADTO(ordenCancelada);

        } catch (Exception e) {
            throw new RuntimeException("Error al cancelar orden: " + e.getMessage());
        }
    }

    // Obtener órdenes por trabajador
    public List<OrdenDTO> obtenerPorTrabajador(Long trabajadorId) {
        try {
            Usuario trabajador = usuarioRepository.findById(trabajadorId)
                .orElseThrow(() -> new RuntimeException("Trabajador no encontrado"));

            List<Orden> ordenes = ordenRepository.findByTrabajadorOrderByFechaCreacionDesc(trabajador);
            return ordenes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener órdenes del trabajador: " + e.getMessage());
        }
    }

    // Buscar órdenes por cliente
    public List<OrdenDTO> buscarPorCliente(String busqueda) {
        try {
            List<Orden> ordenes = ordenRepository.findByClienteEmailContainingIgnoreCaseOrderByFechaCreacionDesc(busqueda);
            return ordenes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar órdenes: " + e.getMessage());
        }
    }

    // Obtener órdenes por rango de fechas
    public List<OrdenDTO> obtenerPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        try {
            List<Orden> ordenes = ordenRepository.findOrdenesPorRangoFechas(fechaInicio, fechaFin);
            return ordenes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener órdenes por fechas: " + e.getMessage());
        }
    }

    // Obtener estadísticas de ventas
    public java.util.Map<String, Object> obtenerEstadisticasVentas() {
        try {
            java.util.Map<String, Object> estadisticas = new java.util.HashMap<>();
            
            long totalOrdenes = ordenRepository.count();
            long ordenesPendientes = ordenRepository.contarOrdenesPorEstado("pendiente");
            long ordenesEnProceso = ordenRepository.contarOrdenesPorEstado("en_proceso");
            long ordenesEntregadas = ordenRepository.contarOrdenesPorEstado("entregado");
            long ordenesCanceladas = ordenRepository.contarOrdenesPorEstado("cancelado");
            
            BigDecimal totalVentas = ordenRepository.calcularTotalVentas();
            if (totalVentas == null) totalVentas = BigDecimal.ZERO;
            
            estadisticas.put("totalOrdenes", totalOrdenes);
            estadisticas.put("ordenesPendientes", ordenesPendientes);
            estadisticas.put("ordenesEnProceso", ordenesEnProceso);
            estadisticas.put("ordenesEntregadas", ordenesEntregadas);
            estadisticas.put("ordenesCanceladas", ordenesCanceladas);
            estadisticas.put("totalVentas", totalVentas);
            
            // Estadísticas por método de pago
            List<Object[]> estadisticasMetodo = ordenRepository.estadisticasPorMetodoPago();
            for (Object[] stat : estadisticasMetodo) {
                String metodo = (String) stat[0];
                Long cantidad = (Long) stat[1];
                BigDecimal total = (BigDecimal) stat[2];
                estadisticas.put("ventas" + metodo.toUpperCase(), total);
                estadisticas.put("ordenes" + metodo.toUpperCase(), cantidad);
            }
            
            return estadisticas;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener estadísticas: " + e.getMessage());
        }
    }

    // Convertir Entity a DTO
    private OrdenDTO convertirADTO(Orden orden) {
        OrdenDTO dto = new OrdenDTO();
        dto.setId(orden.getId());
        dto.setNumeroOrden(orden.getNumeroOrden());
        dto.setEstado(orden.getEstado());
        dto.setTipoAtencion(orden.getTipoAtencion());
        dto.setMetodoPago(orden.getMetodoPago());
        dto.setFechaCreacion(orden.getFechaCreacion());
        dto.setFechaActualizacion(orden.getFechaActualizacion());
        dto.setFechaEntrega(orden.getFechaEntrega());
        dto.setNotas(orden.getNotas());
        
        // Datos del cliente
        dto.setClienteNombre(orden.getClienteNombre());
        dto.setClienteApellido(orden.getClienteApellido());
        dto.setClienteEmail(orden.getClienteEmail());
        dto.setClienteTelefono(orden.getClienteTelefono());
        dto.setClienteDireccion(orden.getClienteDireccion());
        dto.setClienteDni(orden.getClienteDni());
        
        // Datos del usuario
        if (orden.getUsuario() != null) {
            dto.setUsuarioId(orden.getUsuario().getId());
        }
        
        // Datos del trabajador
        if (orden.getTrabajador() != null) {
            dto.setTrabajadorId(orden.getTrabajador().getId());
            dto.setTrabajadorNombre(orden.getTrabajador().getNombre());
            dto.setTrabajadorApellido(orden.getTrabajador().getApellido());
        }
        
        // Convertir items
        if (orden.getItems() != null) {
            List<OrdenItemDTO> itemsDTO = orden.getItems().stream()
                .map(this::convertirItemADTO)
                .collect(Collectors.toList());
            dto.setItems(itemsDTO);
        }
        
        // Calcular totales
        dto.calcularTotales();
        
        return dto;
    }

    // Convertir OrdenItem a DTO
    private OrdenItemDTO convertirItemADTO(OrdenItem item) {
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
        dto.setOrdenId(item.getOrden().getId());
        dto.setNumeroOrden(item.getOrden().getNumeroOrden());
        
        return dto;
    }
}

