
package com.Tecnologia.Repositorio;


import com.Tecnologia.Modelo.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepositorio extends JpaRepository<Rol, Integer> {

    // Buscar un rol por su nombre
    Optional<Rol> findByNombre(String nombre);
}