package com.techsolvers.ecommerce.repository;

import com.techsolvers.ecommerce.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Buscar usuario por email
    Optional<Usuario> findByEmail(String email);
    
    // Buscar usuario por email y password (para login)
    Optional<Usuario> findByEmailAndPassword(String email, String password);
    
    // Buscar usuario por DNI
    Optional<Usuario> findByDni(String dni);
    
    // Verificar si existe email
    boolean existsByEmail(String email);
    
    // Verificar si existe DNI
    boolean existsByDni(String dni);
    
    // Buscar usuarios por rol
    List<Usuario> findByRol(Usuario.Rol rol);
    
    // Buscar usuarios activos
    List<Usuario> findByActivoTrue();
    
    // Buscar usuarios por rol y activos
    List<Usuario> findByRolAndActivoTrue(Usuario.Rol rol);
    
    // Buscar usuarios por nombre o apellido (para búsquedas)
    @Query("SELECT u FROM Usuario u WHERE " +
           "(LOWER(u.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(u.apellido) LIKE LOWER(CONCAT('%', :busqueda, '%'))) AND " +
           "u.activo = true")
    List<Usuario> buscarPorNombreOApellido(@Param("busqueda") String busqueda);
    
    // Contar usuarios por rol
    long countByRol(Usuario.Rol rol);
    
    // Contar usuarios activos
    long countByActivoTrue();
}