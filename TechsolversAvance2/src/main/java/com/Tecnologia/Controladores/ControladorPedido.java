package com.Tecnologia.Controladores;

import com.Tecnologia.Modelo.Carrito;
import com.Tecnologia.Modelo.Clientes;
import com.Tecnologia.Modelo.Item;
import com.Tecnologia.Modelo.Pedido;
import com.Tecnologia.Modelo.PedidoItem;
import com.Tecnologia.Modelo.Producto;
import com.Tecnologia.Repositorio.PedidoRepositorio;
import com.Tecnologia.dao.ProductoRepositorio;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ControladorPedido {
    @Autowired
    private PedidoRepositorio pedidoRepository;

    @Autowired
    private ProductoRepositorio productoRepository;
    
@PostMapping("/pedido")
public String procesarPedido(HttpSession session, Model model) {
    Carrito carrito = (Carrito) session.getAttribute("carrito");
    Clientes cliente = (Clientes) session.getAttribute("cliente");

    if (carrito == null || carrito.getItems().isEmpty() || cliente == null) {
        return "redirect:/carritocompras";
    }

    // Crear el pedido
    Pedido pedido = new Pedido();
    pedido.setCliente(cliente);
    pedido.setFecha(LocalDateTime.now());
    pedido.setEstado("pendiente");

    List<PedidoItem> items = new ArrayList<>();
    BigDecimal totalPedido = BigDecimal.ZERO;

    for (Item item : carrito.getItems()) {
Producto producto = productoRepository.findById(item.getId()).orElseThrow();
        
        PedidoItem pedidoItem = new PedidoItem();
        pedidoItem.setPedido(pedido);
        pedidoItem.setProducto(producto);
        pedidoItem.setCantidad(item.getCantidad());
        pedidoItem.setPrecioUnitario(BigDecimal.valueOf(producto.getPrecio()));
        BigDecimal totalItem = BigDecimal.valueOf(producto.getPrecio())
                                          .multiply(BigDecimal.valueOf(item.getCantidad()));
        pedidoItem.setTotal(totalItem);

        items.add(pedidoItem);
        totalPedido = totalPedido.add(totalItem);
    }

    pedido.setItems(items);
    pedido.setTotal(totalPedido);

    pedidoRepository.save(pedido);

    // Agregar datos al modelo para el resumen
    model.addAttribute("items", carrito.getItems());
    model.addAttribute("total", carrito.getTotal());

    // Vaciar el carrito
    carrito.setItems(new ArrayList<>());
    session.setAttribute("carrito", carrito);

    return "Pedido"; // muestra la vista resumen o éxito
}

}
