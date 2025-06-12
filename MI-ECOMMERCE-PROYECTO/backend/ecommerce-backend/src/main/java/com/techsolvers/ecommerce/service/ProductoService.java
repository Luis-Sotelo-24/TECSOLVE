package com.techsolvers.ecommerce.service;

import com.techsolvers.ecommerce.dto.ProductoDTO;
import com.techsolvers.ecommerce.model.Producto;
import com.techsolvers.ecommerce.model.Categoria;
import com.techsolvers.ecommerce.model.Usuario;
import com.techsolvers.ecommerce.repository.ProductoRepository;
import com.techsolvers.ecommerce.repository.CategoriaRepository;
import com.techsolvers.ecommerce.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Crear nuevo producto
    public ProductoDTO crear(ProductoDTO productoDTO, Long categoriaId, Long creadoPorId) {
        try {
            Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
                
            Usuario creador = usuarioRepository.findById(creadoPorId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Validar que no exista un producto con el mismo nombre
            if (productoRepository.existsByNombreIgnoreCase(productoDTO.getNombre())) {
                throw new RuntimeException("Ya existe un producto con ese nombre");
            }

            Producto producto = new Producto();
            producto.setNombre(productoDTO.getNombre());
            producto.setDescripcion(productoDTO.getDescripcion());
            producto.setPrecio(productoDTO.getPrecio());
            producto.setStock(productoDTO.getStock());
            producto.setStockMinimo(productoDTO.getStockMinimo() != null ? productoDTO.getStockMinimo() : 5);
            producto.setImagenPrincipal(productoDTO.getImagenPrincipal());
            producto.setImagenesAdicionales(productoDTO.getImagenesAdicionales());
            producto.setMarca(productoDTO.getMarca());
            producto.setModelo(productoDTO.getModelo());
            producto.setEspecificacionesTecnicas(productoDTO.getEspecificacionesTecnicas());
            producto.setDestacado(productoDTO.getDestacado() != null ? productoDTO.getDestacado() : false);
            producto.setCategoria(categoria);
            producto.setCreadoPor(creador);
            producto.setActivo(true);

            Producto productoGuardado = productoRepository.save(producto);
            return convertirADTO(productoGuardado);

        } catch (Exception e) {
            throw new RuntimeException("Error al crear producto: " + e.getMessage());
        }
    }

    // Obtener todos los productos activos
    public List<ProductoDTO> listarActivos() {
        try {
            List<Producto> productos = productoRepository.findByActivoTrue();
            return productos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al listar productos: " + e.getMessage());
        }
    }

    // Obtener productos destacados
    public List<ProductoDTO> listarDestacados() {
        try {
            List<Producto> productos = productoRepository.findByDestacadoTrueAndActivoTrue();
            return productos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al listar productos destacados: " + e.getMessage());
        }
    }

    // Obtener producto por ID
    public ProductoDTO obtenerPorId(Long id) {
        try {
            Producto producto = productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            return convertirADTO(producto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener producto: " + e.getMessage());
        }
    }

    // Obtener productos por categoría
    public List<ProductoDTO> obtenerPorCategoria(Long categoriaId) {
        try {
            List<Producto> productos = productoRepository.findByCategoriaId(categoriaId);
            return productos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener productos por categoría: " + e.getMessage());
        }
    }

    // Buscar productos
    public List<ProductoDTO> buscarProductos(String busqueda) {
        try {
            List<Producto> productos = productoRepository.buscarProductos(busqueda);
            return productos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar productos: " + e.getMessage());
        }
    }

    // Buscar por rango de precios
    public List<ProductoDTO> buscarPorPrecio(BigDecimal precioMin, BigDecimal precioMax) {
        try {
            List<Producto> productos = productoRepository.findByPrecioBetweenAndActivoTrue(precioMin, precioMax);
            return productos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar productos por precio: " + e.getMessage());
        }
    }

    // Actualizar producto
    public ProductoDTO actualizar(Long id, ProductoDTO productoDTO) {
        try {
            Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // Validar que no exista otro producto con el mismo nombre
            if (!producto.getNombre().equalsIgnoreCase(productoDTO.getNombre()) &&
                productoRepository.existsByNombreIgnoreCase(productoDTO.getNombre())) {
                throw new RuntimeException("Ya existe un producto con ese nombre");
            }

            producto.setNombre(productoDTO.getNombre());
            producto.setDescripcion(productoDTO.getDescripcion());
            producto.setPrecio(productoDTO.getPrecio());
            producto.setStock(productoDTO.getStock());
            producto.setStockMinimo(productoDTO.getStockMinimo());
            producto.setImagenPrincipal(productoDTO.getImagenPrincipal());
            producto.setImagenesAdicionales(productoDTO.getImagenesAdicionales());
            producto.setMarca(productoDTO.getMarca());
            producto.setModelo(productoDTO.getModelo());
            producto.setEspecificacionesTecnicas(productoDTO.getEspecificacionesTecnicas());
            producto.setDestacado(productoDTO.getDestacado());

            // Actualizar categoría si es necesario
            if (productoDTO.getCategoriaId() != null && 
                !productoDTO.getCategoriaId().equals(producto.getCategoria().getId())) {
                Categoria nuevaCategoria = categoriaRepository.findById(productoDTO.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
                producto.setCategoria(nuevaCategoria);
            }

            Producto productoActualizado = productoRepository.save(producto);
            return convertirADTO(productoActualizado);

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar producto: " + e.getMessage());
        }
    }

    // Eliminar (desactivar) producto
    public boolean eliminar(Long id) {
        try {
            Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            producto.setActivo(false);
            productoRepository.save(producto);
            return true;

        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar producto: " + e.getMessage());
        }
    }

    // Actualizar stock
    public ProductoDTO actualizarStock(Long id, Integer nuevoStock) {
        try {
            Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            producto.setStock(nuevoStock);
            Producto productoActualizado = productoRepository.save(producto);
            return convertirADTO(productoActualizado);

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar stock: " + e.getMessage());
        }
    }

    // Obtener productos con stock bajo
    public List<ProductoDTO> obtenerProductosConStockBajo() {
        try {
            List<Producto> productos = productoRepository.findProductosConStockBajo();
            return productos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener productos con stock bajo: " + e.getMessage());
        }
    }

    // Verificar disponibilidad
    public boolean verificarDisponibilidad(Long id, Integer cantidad) {
        try {
            Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            
            return producto.hayStock(cantidad);
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar disponibilidad: " + e.getMessage());
        }
    }

    // Obtener estadísticas
    public java.util.Map<String, Object> obtenerEstadisticas() {
        try {
            java.util.Map<String, Object> estadisticas = new java.util.HashMap<>();
            
            long totalActivos = productoRepository.contarProductosActivos();
            long totalInactivos = productoRepository.count() - totalActivos;
            long totalStockBajo = productoRepository.findProductosConStockBajo().size();
            
            estadisticas.put("totalActivos", totalActivos);
            estadisticas.put("totalInactivos", totalInactivos);
            estadisticas.put("totalStockBajo", totalStockBajo);
            estadisticas.put("totalGeneral", productoRepository.count());
            
            return estadisticas;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener estadísticas: " + e.getMessage());
        }
    }

    // Convertir Entity a DTO
    private ProductoDTO convertirADTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setStockMinimo(producto.getStockMinimo());
        dto.setImagenPrincipal(producto.getImagenPrincipal());
        dto.setImagenesAdicionales(producto.getImagenesAdicionales());
        dto.setMarca(producto.getMarca());
        dto.setModelo(producto.getModelo());
        dto.setEspecificacionesTecnicas(producto.getEspecificacionesTecnicas());
        dto.setDestacado(producto.getDestacado());
        dto.setActivo(producto.getActivo());
        dto.setFechaCreacion(producto.getFechaCreacion());
        dto.setFechaActualizacion(producto.getFechaActualizacion());
        
        // Datos de la categoría
        if (producto.getCategoria() != null) {
            dto.setCategoriaId(producto.getCategoria().getId());
            dto.setCategoriaTitulo(producto.getCategoria().getTitulo());
            dto.setCategoriaImagenUrl(producto.getCategoria().getImagenUrl());
        }
        
        // Datos del creador
        if (producto.getCreadoPor() != null) {
            dto.setCreadoPorId(producto.getCreadoPor().getId());
            dto.setCreadoPorNombre(producto.getCreadoPor().getNombre());
            dto.setCreadoPorApellido(producto.getCreadoPor().getApellido());
        }
        
        // Información calculada
        dto.setHayStock(producto.getStock() != null && producto.getStock() > 0);
        dto.setEnStockMinimo(producto.enStockMinimo());
        
        return dto;
    }
}


