package com.techsolvers.ecommerce.repository;

import com.techsolvers.ecommerce.model.Orden;
import com.techsolvers.ecommerce.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
    
    // Buscar por número de orden
    Optional<Orden> findByNumeroOrden(String numeroOrden);
    
    // Buscar órdenes por usuario
    List<Orden> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);
    
    // Buscar órdenes por estado
    List<Orden> findByEstadoOrderByFechaCreacionDesc(String estado);
    
    // Buscar órdenes por trabajador
    List<Orden> findByTrabajadorOrderByFechaCreacionDesc(Usuario trabajador);
    
    // Buscar órdenes por tipo de atención
    List<Orden> findByTipoAtencionOrderByFechaCreacionDesc(String tipoAtencion);
    
    // Buscar órdenes por método de pago
    List<Orden> findByMetodoPagoOrderByFechaCreacionDesc(String metodoPago);
    
    // Buscar órdenes por usuario y estado
    List<Orden> findByUsuarioAndEstadoOrderByFechaCreacionDesc(Usuario usuario, String estado);
    
    // Buscar órdenes entre fechas
    @Query("SELECT o FROM Orden o WHERE o.fechaCreacion BETWEEN :fechaInicio AND :fechaFin ORDER BY o.fechaCreacion DESC")
    List<Orden> findOrdenesPorRangoFechas(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                         @Param("fechaFin") LocalDateTime fechaFin);
    
    // Buscar órdenes por rango de totales
    List<Orden> findByTotalBetweenOrderByFechaCreacionDesc(BigDecimal totalMin, BigDecimal totalMax);
    
    // Contar órdenes por estado
    @Query("SELECT COUNT(o) FROM Orden o WHERE o.estado = :estado")
    long contarOrdenesPorEstado(@Param("estado") String estado);
    
    // Calcular total de ventas
    @Query("SELECT SUM(o.total) FROM Orden o WHERE o.estado IN ('en_proceso', 'entregado')")
    BigDecimal calcularTotalVentas();
    
    // Calcular total de ventas por período
    @Query("SELECT SUM(o.total) FROM Orden o WHERE o.estado IN ('en_proceso', 'entregado') AND o.fechaCreacion BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal calcularVentasPorPeriodo(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                       @Param("fechaFin") LocalDateTime fechaFin);
    
    // Órdenes pendientes
    @Query("SELECT o FROM Orden o WHERE o.estado = 'pendiente' ORDER BY o.fechaCreacion ASC")
    List<Orden> findOrdenesPendientes();
    
    // Últimas órdenes
    @Query("SELECT o FROM Orden o ORDER BY o.fechaCreacion DESC")
    List<Orden> findUltimasOrdenes();
    
    // Estadísticas por método de pago
    @Query("SELECT o.metodoPago, COUNT(o), SUM(o.total) FROM Orden o WHERE o.estado IN ('en_proceso', 'entregado') GROUP BY o.metodoPago")
    List<Object[]> estadisticasPorMetodoPago();
    
    // Ventas por trabajador
    @Query("SELECT o.trabajador, COUNT(o), SUM(o.total) FROM Orden o WHERE o.trabajador IS NOT NULL AND o.estado IN ('en_proceso', 'entregado') GROUP BY o.trabajador")
    List<Object[]> ventasPorTrabajador();
    
    // Buscar por cliente email
    List<Orden> findByClienteEmailContainingIgnoreCaseOrderByFechaCreacionDesc(String email);
    
    // Órdenes del día
    @Query("SELECT o FROM Orden o WHERE DATE(o.fechaCreacion) = CURRENT_DATE ORDER BY o.fechaCreacion DESC")
    List<Orden> findOrdenesDelDia();
}

