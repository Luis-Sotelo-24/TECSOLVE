package com.techsolvers.ecommerce.repository;

import com.techsolvers.ecommerce.model.Carrito;
import com.techsolvers.ecommerce.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    
    // Buscar carrito activo del usuario
    Optional<Carrito> findByUsuarioAndEstado(Usuario usuario, String estado);
    
    // Buscar carrito activo del usuario (método específico)
    @Query("SELECT c FROM Carrito c WHERE c.usuario = :usuario AND c.estado = 'activo'")
    Optional<Carrito> findCarritoActivoByUsuario(@Param("usuario") Usuario usuario);
    
    // Buscar carritos por session ID (usuarios no registrados)
    Optional<Carrito> findBySessionIdAndEstado(String sessionId, String estado);
    
    // Buscar carritos por trabajador
    List<Carrito> findByTrabajador(Usuario trabajador);
    
    // Buscar carritos por tipo de atención
    List<Carrito> findByTipoAtencion(String tipoAtencion);
    
    // Buscar carritos por estado
    List<Carrito> findByEstado(String estado);
    
    // Buscar carritos abandonados (más de X días)
    @Query("SELECT c FROM Carrito c WHERE c.estado = 'activo' AND c.fechaActualizacion < :fechaLimite")
    List<Carrito> findCarritosAbandonados(@Param("fechaLimite") LocalDateTime fechaLimite);
    
    // Contar carritos activos
    @Query("SELECT COUNT(c) FROM Carrito c WHERE c.estado = 'activo'")
    long contarCarritosActivos();
    
    // Buscar carritos del usuario por estado
    List<Carrito> findByUsuarioAndEstadoOrderByFechaCreacionDesc(Usuario usuario, String estado);
    
    // Buscar todos los carritos del usuario
    List<Carrito> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);
    
    // Carritos presenciales por trabajador
    @Query("SELECT c FROM Carrito c WHERE c.trabajador = :trabajador AND c.tipoAtencion = 'presencial'")
    List<Carrito> findCarritosPresencialesByTrabajador(@Param("trabajador") Usuario trabajador);
    
    // Estadísticas de carritos por tipo de atención
    @Query("SELECT c.tipoAtencion, COUNT(c) FROM Carrito c GROUP BY c.tipoAtencion")
    List<Object[]> estadisticasPorTipoAtencion();
    
    // Carritos con items (no vacíos)
    @Query("SELECT DISTINCT c FROM Carrito c JOIN c.items i WHERE c.estado = 'activo'")
    List<Carrito> findCarritosConItems();
}

