package com.techsolvers.ecommerce.repository;

import com.techsolvers.ecommerce.model.Producto;
import com.techsolvers.ecommerce.model.Categoria;
import com.techsolvers.ecommerce.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // Buscar productos activos
    List<Producto> findByActivoTrue();
    
    // Buscar productos destacados
    List<Producto> findByDestacadoTrueAndActivoTrue();
    
    // Buscar por categoría
    List<Producto> findByCategoriaAndActivoTrue(Categoria categoria);
    
    // Buscar por nombre (case insensitive)
    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
    
    // Buscar por marca
    List<Producto> findByMarcaIgnoreCaseAndActivoTrue(String marca);
    
    // Buscar por rango de precios
    List<Producto> findByPrecioBetweenAndActivoTrue(BigDecimal precioMin, BigDecimal precioMax);
    
    // Buscar por usuario que lo creó
    List<Producto> findByCreadoPor(Usuario usuario);
    
    // Buscar productos con stock bajo
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo AND p.activo = true")
    List<Producto> findProductosConStockBajo();
    
    // Buscar productos sin stock
    List<Producto> findByStockAndActivoTrue(Integer stock);
    
    // Verificar si existe producto con ese nombre
    boolean existsByNombreIgnoreCase(String nombre);
    
    // Buscar producto activo por ID
    Optional<Producto> findByIdAndActivoTrue(Long id);
    
    // Búsqueda avanzada (nombre, descripción, marca)
    @Query("SELECT p FROM Producto p WHERE p.activo = true AND " +
           "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(p.marca) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    List<Producto> buscarProductos(@Param("busqueda") String busqueda);
    
    // Contar productos activos
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.activo = true")
    long contarProductosActivos();
    
    // Productos más vendidos (simulado por ahora)
    @Query("SELECT p FROM Producto p WHERE p.activo = true ORDER BY p.id DESC")
    List<Producto> findProductosMasVendidos();
    
    // Productos por categoría ID
    @Query("SELECT p FROM Producto p WHERE p.categoria.id = :categoriaId AND p.activo = true")
    List<Producto> findByCategoriaId(@Param("categoriaId") Long categoriaId);
    
    // Últimos productos agregados
    @Query("SELECT p FROM Producto p WHERE p.activo = true ORDER BY p.fechaCreacion DESC")
    List<Producto> findUltimosProductos();
}
