package com.Tecnologia.Repositorio;

import com.Tecnologia.Modelo.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PedidoRepositorio extends JpaRepository<Pedido, Long>{
    
}
