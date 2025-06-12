package com.techsolvers.ecommerce.service;

import com.techsolvers.ecommerce.dto.CarritoDTO;
import com.techsolvers.ecommerce.dto.CarritoItemDTO;
import com.techsolvers.ecommerce.model.Carrito;
import com.techsolvers.ecommerce.model.CarritoItem;
import com.techsolvers.ecommerce.model.Producto;
import com.techsolvers.ecommerce.model.Usuario;
import com.techsolvers.ecommerce.repository.CarritoRepository;
import com.techsolvers.ecommerce.repository.CarritoItemRepository;
import com.techsolvers.ecommerce.repository.ProductoRepository;
import com.techsolvers.ecommerce.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;
    
    @Autowired
    private CarritoItemRepository carritoItemRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Obtener o crear carrito activo del usuario
    public CarritoDTO obtenerCarritoActivo(Long usuarioId) {
        try {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Optional<Carrito> carritoExistente = carritoRepository.findCarritoActivoByUsuario(usuario);
            
            Carrito carrito;
            if (carritoExistente.isPresent()) {
                carrito = carritoExistente.get();
            } else {
                // Crear nuevo carrito
                carrito = new Carrito(usuario);
                carrito = carritoRepository.save(carrito);
            }

            return convertirADTO(carrito);

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener carrito: " + e.getMessage());
        }
    }

    // Agregar producto al carrito
    public CarritoDTO agregarProducto(Long usuarioId, Long productoId, Integer cantidad) {
        try {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Producto producto = productoRepository.findByIdAndActivoTrue(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // Verificar stock disponible
            if (!producto.hayStock(cantidad)) {
                throw new RuntimeException("Stock insuficiente. Stock disponible: " + producto.getStock());
            }

            // Obtener o crear carrito
            Carrito carrito = carritoRepository.findCarritoActivoByUsuario(usuario)
                .orElse(new Carrito(usuario));
            
            if (carrito.getId() == null) {
                carrito = carritoRepository.save(carrito);
            }

            // Verificar si el producto ya está en el carrito
            Optional<CarritoItem> itemExistente = carritoItemRepository.findByCarritoAndProducto(carrito, producto);
            
            if (itemExistente.isPresent()) {
                // Actualizar cantidad
                CarritoItem item = itemExistente.get();
                int nuevaCantidad = item.getCantidad() + cantidad;
                
                if (!producto.hayStock(nuevaCantidad)) {
                    throw new RuntimeException("Stock insuficiente para la cantidad total solicitada");
                }
                
                item.setCantidad(nuevaCantidad);
                item.calcularSubtotal();
                carritoItemRepository.save(item);
            } else {
                // Crear nuevo item
                CarritoItem nuevoItem = new CarritoItem(carrito, producto, cantidad);
                carritoItemRepository.save(nuevoItem);
            }

            // Recalcular total del carrito
            carrito.calcularTotal();
            carritoRepository.save(carrito);

            return convertirADTO(carrito);

        } catch (Exception e) {
            throw new RuntimeException("Error al agregar producto al carrito: " + e.getMessage());
        }
    }

    // Actualizar cantidad de producto en carrito
    public CarritoDTO actualizarCantidad(Long usuarioId, Long productoId, Integer nuevaCantidad) {
        try {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Carrito carrito = carritoRepository.findCarritoActivoByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

            Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            CarritoItem item = carritoItemRepository.findByCarritoAndProducto(carrito, producto)
                .orElseThrow(() -> new RuntimeException("Producto no está en el carrito"));

            if (nuevaCantidad <= 0) {
                // Eliminar item del carrito
                carritoItemRepository.delete(item);
            } else {
                // Verificar stock disponible
                if (!producto.hayStock(nuevaCantidad)) {
                    throw new RuntimeException("Stock insuficiente. Stock disponible: " + producto.getStock());
                }

                item.setCantidad(nuevaCantidad);
                item.calcularSubtotal();
                carritoItemRepository.save(item);
            }

            // Recalcular total del carrito
            carrito.calcularTotal();
            carritoRepository.save(carrito);

            return convertirADTO(carrito);

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar cantidad: " + e.getMessage());
        }
    }

    // Eliminar producto del carrito
    public CarritoDTO eliminarProducto(Long usuarioId, Long productoId) {
        try {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Carrito carrito = carritoRepository.findCarritoActivoByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

            Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            CarritoItem item = carritoItemRepository.findByCarritoAndProducto(carrito, producto)
                .orElseThrow(() -> new RuntimeException("Producto no está en el carrito"));

            carritoItemRepository.delete(item);

            // Recalcular total del carrito
            carrito.calcularTotal();
            carritoRepository.save(carrito);

            return convertirADTO(carrito);

        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar producto del carrito: " + e.getMessage());
        }
    }

    // Vaciar carrito
    public CarritoDTO vaciarCarrito(Long usuarioId) {
        try {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Carrito carrito = carritoRepository.findCarritoActivoByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

            carritoItemRepository.deleteByCarrito(carrito);
            carrito.calcularTotal();
            carritoRepository.save(carrito);

            return convertirADTO(carrito);

        } catch (Exception e) {
            throw new RuntimeException("Error al vaciar carrito: " + e.getMessage());
        }
    }

    // Crear carrito presencial (atendido por trabajador)
    public CarritoDTO crearCarritoPresencial(Long usuarioId, Long trabajadorId) {
        try {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Usuario trabajador = usuarioRepository.findById(trabajadorId)
                .orElseThrow(() -> new RuntimeException("Trabajador no encontrado"));

            Carrito carrito = new Carrito(usuario, trabajador);
            carrito = carritoRepository.save(carrito);

            return convertirADTO(carrito);

        } catch (Exception e) {
            throw new RuntimeException("Error al crear carrito presencial: " + e.getMessage());
        }
    }

    // Obtener carritos por trabajador
    public List<CarritoDTO> obtenerCarritosPorTrabajador(Long trabajadorId) {
        try {
            Usuario trabajador = usuarioRepository.findById(trabajadorId)
                .orElseThrow(() -> new RuntimeException("Trabajador no encontrado"));

            List<Carrito> carritos = carritoRepository.findCarritosPresencialesByTrabajador(trabajador);
            return carritos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener carritos del trabajador: " + e.getMessage());
        }
    }

    // Obtener carritos abandonados
    public List<CarritoDTO> obtenerCarritosAbandonados(int diasLimite) {
        try {
            LocalDateTime fechaLimite = LocalDateTime.now().minusDays(diasLimite);
            List<Carrito> carritos = carritoRepository.findCarritosAbandonados(fechaLimite);
            return carritos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener carritos abandonados: " + e.getMessage());
        }
    }

    // Obtener estadísticas
    public java.util.Map<String, Object> obtenerEstadisticas() {
        try {
            java.util.Map<String, Object> estadisticas = new java.util.HashMap<>();
            
            long totalActivos = carritoRepository.contarCarritosActivos();
            long totalConItems = carritoRepository.findCarritosConItems().size();
            List<Object[]> estadisticasTipo = carritoRepository.estadisticasPorTipoAtencion();
            
            estadisticas.put("totalActivos", totalActivos);
            estadisticas.put("totalConItems", totalConItems);
            estadisticas.put("totalGeneral", carritoRepository.count());
            
            // Agregar estadísticas por tipo
            for (Object[] stat : estadisticasTipo) {
                String tipo = (String) stat[0];
                Long cantidad = (Long) stat[1];
                estadisticas.put("total" + tipo.substring(0, 1).toUpperCase() + tipo.substring(1), cantidad);
            }
            
            return estadisticas;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener estadísticas: " + e.getMessage());
        }
    }

    // Convertir Entity a DTO
    private CarritoDTO convertirADTO(Carrito carrito) {
        CarritoDTO dto = new CarritoDTO();
        dto.setId(carrito.getId());
        dto.setEstado(carrito.getEstado());
        dto.setTipoAtencion(carrito.getTipoAtencion());
        dto.setTotal(carrito.getTotal());
        dto.setFechaCreacion(carrito.getFechaCreacion());
        dto.setFechaActualizacion(carrito.getFechaActualizacion());
        dto.setSessionId(carrito.getSessionId());
        
        // Datos del usuario
        if (carrito.getUsuario() != null) {
            dto.setUsuarioId(carrito.getUsuario().getId());
            dto.setUsuarioNombre(carrito.getUsuario().getNombre());
            dto.setUsuarioApellido(carrito.getUsuario().getApellido());
            dto.setUsuarioEmail(carrito.getUsuario().getEmail());
        }
        
        // Datos del trabajador
        if (carrito.getTrabajador() != null) {
            dto.setTrabajadorId(carrito.getTrabajador().getId());
            dto.setTrabajadorNombre(carrito.getTrabajador().getNombre());
            dto.setTrabajadorApellido(carrito.getTrabajador().getApellido());
        }
        
        // Convertir items
        if (carrito.getItems() != null) {
            List<CarritoItemDTO> itemsDTO = carrito.getItems().stream()
                .map(this::convertirItemADTO)
                .collect(Collectors.toList());
            dto.setItems(itemsDTO);
        }
        
        // Calcular totales
        dto.calcularTotales();
        
        return dto;
    }

    // Convertir CarritoItem a DTO
    private CarritoItemDTO convertirItemADTO(CarritoItem item) {
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



