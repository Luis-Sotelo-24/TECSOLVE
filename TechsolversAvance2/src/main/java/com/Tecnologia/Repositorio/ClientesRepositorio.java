
package com.Tecnologia.Repositorio;
import com.Tecnologia.Modelo.Clientes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientesRepositorio extends JpaRepository<Clientes, Integer> {

    Optional<Clientes> findByCorreo(String correo);
}