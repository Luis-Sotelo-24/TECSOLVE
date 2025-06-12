package com.techsolvers.ecommerce.repository;

import com.techsolvers.ecommerce.model.CarritoItem;
import com.techsolvers.ecommerce.model.Carrito;
import com.techsolvers.ecommerce.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    
    // Buscar items por carrito
    List<CarritoItem> findByCarrito(Carrito carrito);
    
    // Buscar item específico en carrito
    Optional<CarritoItem> findByCarritoAndProducto(Carrito carrito, Producto producto);
    
    // Buscar items por producto
    List<CarritoItem> findByProducto(Producto producto);
    
    // Contar items en carrito
    @Query("SELECT COUNT(ci) FROM CarritoItem ci WHERE ci.carrito = :carrito")
    long contarItemsEnCarrito(@Param("carrito") Carrito carrito);
    
    // Calcular total de cantidad en carrito
    @Query("SELECT SUM(ci.cantidad) FROM CarritoItem ci WHERE ci.carrito = :carrito")
    Integer calcularCantidadTotalEnCarrito(@Param("carrito") Carrito carrito);
    
    // Calcular total de subtotales en carrito
    @Query("SELECT SUM(ci.subtotal) FROM CarritoItem ci WHERE ci.carrito = :carrito")
    java.math.BigDecimal calcularTotalCarrito(@Param("carrito") Carrito carrito);
    
    // Buscar items por carrito ordenados por fecha
    List<CarritoItem> findByCarritoOrderByFechaAgregadoDesc(Carrito carrito);
    
    // Verificar si producto está en carrito
    boolean existsByCarritoAndProducto(Carrito carrito, Producto producto);
    
    // Eliminar items por carrito
    void deleteByCarrito(Carrito carrito);
    
    // Productos más agregados al carrito
    @Query("SELECT ci.producto, COUNT(ci) as cantidad FROM CarritoItem ci GROUP BY ci.producto ORDER BY cantidad DESC")
    List<Object[]> findProductosMasAgregados();
    
    // Items con cantidad mayor a X
    @Query("SELECT ci FROM CarritoItem ci WHERE ci.cantidad > :cantidad")
    List<CarritoItem> findItemsConCantidadMayorA(@Param("cantidad") Integer cantidad);
}

