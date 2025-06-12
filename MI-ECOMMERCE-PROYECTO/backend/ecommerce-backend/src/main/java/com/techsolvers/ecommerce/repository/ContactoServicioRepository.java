package com.techsolvers.ecommerce.repository;

import com.techsolvers.ecommerce.model.ContactoServicio;
import com.techsolvers.ecommerce.model.Servicio;
import com.techsolvers.ecommerce.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContactoServicioRepository extends JpaRepository<ContactoServicio, Long> {
    
    // Buscar por servicio
    List<ContactoServicio> findByServicioOrderByFechaCreacionDesc(Servicio servicio);
    
    // Buscar por estado
    List<ContactoServicio> findByEstadoOrderByFechaCreacionDesc(String estado);
    
    // Buscar por usuario que atendió
    List<ContactoServicio> findByAtendidoPorOrderByFechaCreacionDesc(Usuario atendidoPor);
    
    // Buscar por rango de fechas
    List<ContactoServicio> findByFechaCreacionBetweenOrderByFechaCreacionDesc(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    // Buscar por nombre o email
    @Query("SELECT c FROM ContactoServicio c WHERE " +
           "LOWER(c.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(c.apellido) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    List<ContactoServicio> buscarPorNombreOEmail(@Param("busqueda") String busqueda);
    
    // Contar por estado
    @Query("SELECT COUNT(c) FROM ContactoServicio c WHERE c.estado = :estado")
    long contarPorEstado(@Param("estado") String estado);
    
    // Estadísticas por servicio
    @Query("SELECT s.titulo, COUNT(c) FROM ContactoServicio c JOIN c.servicio s GROUP BY s.titulo")
    List<Object[]> estadisticasPorServicio();
    
    // Contactos del día
    @Query("SELECT c FROM ContactoServicio c WHERE DATE(c.fechaCreacion) = CURRENT_DATE ORDER BY c.fechaCreacion DESC")
    List<ContactoServicio> findContactosDelDia();
    
    // Contactos pendientes
    @Query("SELECT c FROM ContactoServicio c WHERE c.estado = 'pendiente' ORDER BY c.fechaCreacion ASC")
    List<ContactoServicio> findContactosPendientes();
}

