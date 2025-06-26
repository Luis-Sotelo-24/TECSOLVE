package com.Tecnologia.Repositorio;

import com.Tecnologia.Modelo.PedidoItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PedidoItemRepositorio extends JpaRepository<PedidoItem, Long> {
    List<PedidoItem> findByPedidoIdPedido(Long id_pedido);
}
