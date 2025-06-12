package com.techsolvers.ecommerce.repository;

import com.techsolvers.ecommerce.model.OrdenItem;
import com.techsolvers.ecommerce.model.Orden;
import com.techsolvers.ecommerce.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrdenItemRepository extends JpaRepository<OrdenItem, Long> {
    
    // Buscar items por orden
    List<OrdenItem> findByOrden(Orden orden);
    
    // Buscar items por producto
    List<OrdenItem> findByProducto(Producto producto);
    
    // Contar items en orden
    @Query("SELECT COUNT(oi) FROM OrdenItem oi WHERE oi.orden = :orden")
    long contarItemsEnOrden(@Param("orden") Orden orden);
    
    // Calcular total de cantidad en orden
    @Query("SELECT SUM(oi.cantidad) FROM OrdenItem oi WHERE oi.orden = :orden")
    Integer calcularCantidadTotalEnOrden(@Param("orden") Orden orden);
    
    // Calcular total de subtotales en orden
    @Query("SELECT SUM(oi.subtotal) FROM OrdenItem oi WHERE oi.orden = :orden")
    java.math.BigDecimal calcularTotalOrden(@Param("orden") Orden orden);
    
    // Productos más vendidos
    @Query("SELECT oi.producto, SUM(oi.cantidad) as totalVendido FROM OrdenItem oi " +
           "JOIN oi.orden o WHERE o.estado IN ('en_proceso', 'entregado') " +
           "GROUP BY oi.producto ORDER BY totalVendido DESC")
    List<Object[]> findProductosMasVendidos();
    
    // Productos más vendidos en período
    @Query("SELECT oi.producto, SUM(oi.cantidad) as totalVendido FROM OrdenItem oi " +
           "JOIN oi.orden o WHERE o.estado IN ('en_proceso', 'entregado') " +
           "AND o.fechaCreacion BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY oi.producto ORDER BY totalVendido DESC")
    List<Object[]> findProductosMasVendidosPorPeriodo(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                                     @Param("fechaFin") LocalDateTime fechaFin);
    
    // Ingresos por producto
    @Query("SELECT oi.producto, SUM(oi.subtotal) as totalIngresos FROM OrdenItem oi " +
           "JOIN oi.orden o WHERE o.estado IN ('en_proceso', 'entregado') " +
           "GROUP BY oi.producto ORDER BY totalIngresos DESC")
    List<Object[]> findIngresosPorProducto();
    
    // Cantidad total vendida de un producto
    @Query("SELECT SUM(oi.cantidad) FROM OrdenItem oi JOIN oi.orden o " +
           "WHERE oi.producto = :producto AND o.estado IN ('en_proceso', 'entregado')")
    Integer calcularCantidadVendidaDeProducto(@Param("producto") Producto producto);
    
    // Items vendidos hoy
    @Query("SELECT oi FROM OrdenItem oi JOIN oi.orden o " +
           "WHERE DATE(o.fechaCreacion) = CURRENT_DATE AND o.estado IN ('en_proceso', 'entregado')")
    List<OrdenItem> findItemsVendidosHoy();
    
    // Productos vendidos por trabajador
    @Query("SELECT oi.producto, SUM(oi.cantidad) FROM OrdenItem oi " +
           "JOIN oi.orden o WHERE o.trabajador = :trabajador AND o.estado IN ('en_proceso', 'entregado') " +
           "GROUP BY oi.producto")
    List<Object[]> findProductosVendidosPorTrabajador(@Param("trabajador") com.techsolvers.ecommerce.model.Usuario trabajador);
    
    // Estadísticas de ventas por marca
    @Query("SELECT oi.productoMarca, COUNT(oi), SUM(oi.cantidad), SUM(oi.subtotal) FROM OrdenItem oi " +
           "JOIN oi.orden o WHERE o.estado IN ('en_proceso', 'entregado') " +
           "GROUP BY oi.productoMarca ORDER BY SUM(oi.subtotal) DESC")
    List<Object[]> estadisticasVentasPorMarca();
}



