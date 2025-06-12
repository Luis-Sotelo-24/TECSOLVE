package com.techsolvers.ecommerce.repository;

import com.techsolvers.ecommerce.model.Categoria;
import com.techsolvers.ecommerce.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    // Buscar categorías activas
    List<Categoria> findByActivoTrue();
    
    // Buscar por título (case insensitive)
    List<Categoria> findByTituloContainingIgnoreCase(String titulo);
    
    // Buscar por usuario que la creó
    List<Categoria> findByCreadoPor(Usuario usuario);
    
    // Buscar categorías activas por usuario
    List<Categoria> findByActivoTrueAndCreadoPor(Usuario usuario);
    
    // Verificar si existe una categoría con ese título
    boolean existsByTituloIgnoreCase(String titulo);
    
    // Buscar categoría activa por ID
    Optional<Categoria> findByIdAndActivoTrue(Long id);
    
    // Contar categorías activas
    @Query("SELECT COUNT(c) FROM Categoria c WHERE c.activo = true")
    long contarCategoriasActivas();
    
    // Obtener categorías con productos
    @Query("SELECT DISTINCT c FROM Categoria c JOIN c.productos p WHERE c.activo = true AND p.activo = true")
    List<Categoria> findCategoriasConProductos();
    
    // Buscar categorías por rango de fechas
    @Query("SELECT c FROM Categoria c WHERE c.fechaCreacion BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fechaCreacion DESC")
    List<Categoria> findByFechaCreacionBetween(@Param("fechaInicio") java.time.LocalDateTime fechaInicio, 
                                              @Param("fechaFin") java.time.LocalDateTime fechaFin);
}

