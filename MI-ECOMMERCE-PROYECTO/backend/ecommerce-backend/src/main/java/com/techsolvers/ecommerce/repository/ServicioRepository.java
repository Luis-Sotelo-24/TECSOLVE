package com.techsolvers.ecommerce.repository;

import com.techsolvers.ecommerce.model.Servicio;
import com.techsolvers.ecommerce.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    
    // Buscar servicios activos
    List<Servicio> findByActivoTrue();
    
    // Buscar servicios que se muestran en inicio
    List<Servicio> findByMostrarEnInicioTrueAndActivoTrue();
    
    // Buscar por tipo de servicio
    List<Servicio> findByTipoServicioAndActivoTrue(String tipoServicio);
    
    // Buscar por título (case insensitive)
    List<Servicio> findByTituloContainingIgnoreCaseAndActivoTrue(String titulo);
    
    // Buscar por usuario que lo creó
    List<Servicio> findByCreadoPor(Usuario usuario);
    
    // Buscar servicios activos por usuario
    List<Servicio> findByActivoTrueAndCreadoPor(Usuario usuario);
    
    // Verificar si existe servicio con ese título
    boolean existsByTituloIgnoreCase(String titulo);
    
    // Buscar servicio activo por ID
    Optional<Servicio> findByIdAndActivoTrue(Long id);
    
    // Contar servicios activos
    @Query("SELECT COUNT(s) FROM Servicio s WHERE s.activo = true")
    long contarServiciosActivos();
    
    // Servicios por tipo específico
    @Query("SELECT s FROM Servicio s WHERE s.tipoServicio = :tipo AND s.activo = true ORDER BY s.fechaCreacion DESC")
    List<Servicio> findServiciosPorTipo(@Param("tipo") String tipo);
    
    // Búsqueda en título y descripción
    @Query("SELECT s FROM Servicio s WHERE s.activo = true AND " +
           "(LOWER(s.titulo) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(s.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    List<Servicio> buscarServicios(@Param("busqueda") String busqueda);
    
    // Últimos servicios creados
    @Query("SELECT s FROM Servicio s WHERE s.activo = true ORDER BY s.fechaCreacion DESC")
    List<Servicio> findUltimosServicios();
    
    // Servicios con precios
    @Query("SELECT s FROM Servicio s WHERE s.precioDesde IS NOT NULL AND s.activo = true ORDER BY s.precioDesde ASC")
    List<Servicio> findServiciosConPrecios();
}




